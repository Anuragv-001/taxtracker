import { Form90CTransaction } from "../model/Form90C";
import { VALIDATION_MESSAGES } from "../messages/validationMessages";

// ================= FULL NAME =================
export const validateFullName = (name: string) => {
  if (!name.trim()) {
    return VALIDATION_MESSAGES.fullName.required;
  }

  const regex = /^[A-Z][a-z]+ [A-Z][a-z]+$/;
  if (!regex.test(name)) {
    return VALIDATION_MESSAGES.fullName.format;
  }

  return "";
};

// ================= MOBILE NUMBER =================
export const validateMobile = (mobile: string) => {
  if (!mobile) {
    return VALIDATION_MESSAGES.mobile.required;
  }

  const regex = /^[6-9]\d{9}$/;
  if (!regex.test(mobile)) {
    return VALIDATION_MESSAGES.mobile.invalid;
  }

  return "";
};

// ================= FINANCIAL YEAR =================
export const validateFinancialYear = (fy: string) => {
  if (!fy.trim()) {
    return VALIDATION_MESSAGES.financialYear.required;
  }

  const fyRegex = /^\d{4}-\d{4}$/;
  if (!fyRegex.test(fy)) {
    return VALIDATION_MESSAGES.financialYear.format;
  }

  const [startYearStr, endYearStr] = fy.split("-");
  const startYear = Number(startYearStr);
  const endYear = Number(endYearStr);

  if (startYear >= endYear) {
    return VALIDATION_MESSAGES.financialYear.order;
  }

  if (endYear - startYear !== 1) {
    return VALIDATION_MESSAGES.financialYear.span;
  }

  return "";
};

// ================= TRANSACTION DATE WITH FY =================
export const validateTransactionDateWithFY = (
  transactionDate: string,
  financialYear: string
) => {
  if (!transactionDate || !financialYear) return "";

  const [startYear, endYear] = financialYear.split("-").map(Number);

  const startDate = new Date(startYear, 3, 1); // April
  const endDate = new Date(endYear, 2, 31);   // March

  const txDate = new Date(transactionDate);

  if (txDate < startDate || txDate > endDate) {
    return VALIDATION_MESSAGES.financialYear.transactionDateRange(financialYear);
  }

  return "";
};

// ================= TRANSACTION ROW =================
export const validateTransactionRow = (
  row: Form90CTransaction,
  financialYear: string
) => {
  const errors: any = {};

  if (!row.txnDate) {
    errors.transactionDate =
      VALIDATION_MESSAGES.transaction.dateRequired;
  } else if (financialYear) {
    const [startYear, endYear] = financialYear.split("-").map(Number);

    const fyStart = new Date(startYear, 3, 1);
    const fyEnd = new Date(endYear, 2, 31);

    const txDate = new Date(row.txnDate);

    if (txDate < fyStart || txDate > fyEnd) {
      errors.transactionDate =
        VALIDATION_MESSAGES.transaction.dateOutOfFY(financialYear);
    }
  }

  if (!row.organizationName.trim()) {
    errors.organizationName =
      VALIDATION_MESSAGES.transaction.organizationRequired;
  }

  if (row.amount === "" || row.amount == null) {
    errors.amount = VALIDATION_MESSAGES.transaction.amountRequired;
  } else if (Number(row.amount) <= 0) {
    errors.amount = VALIDATION_MESSAGES.transaction.amountPositive;
  }

  if (row.taxAmount === "" || row.taxAmount == null) {
    errors.taxAmount = VALIDATION_MESSAGES.transaction.taxRequired;
  } else if (Number(row.taxAmount) < 0) {
    errors.taxAmount = VALIDATION_MESSAGES.transaction.taxNonNegative;
  } else if (Number(row.taxAmount) > Number(row.amount)) {
    errors.taxAmount =
      VALIDATION_MESSAGES.transaction.taxLessThanAmount;
  }
  if (!row.type || row.type.trim() === "") {
    errors.type = VALIDATION_MESSAGES.transaction.typeRequired;
  }
  return errors;
};
