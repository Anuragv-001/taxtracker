export const VALIDATION_MESSAGES = {
  fullName: {
    required: "Full name is required",
    format: "Enter first & last name with capital letters",
  },

  mobile: {
    required: "Mobile number is required",
    invalid: "Enter valid 10-digit mobile number",
  },

  financialYear: {
    required: "Financial year is required",
    format: "Financial year must be in format YYYY-YYYY",
    order: "Start year must be less than end year",
    span: "Financial year must span exactly one year (e.g. 2022-2023)",
    transactionDateRange: (fy: string) =>
      `Transaction date must fall within financial year ${fy}`,
  },

  transaction: {
    dateRequired: "Transaction date is required",
    dateOutOfFY: (fy: string) =>
      `Transaction date must fall within FY ${fy}`,

    organizationRequired: "Organization name is required",

    amountRequired: "Amount is required",
    amountPositive: "Amount must be greater than 0",

    taxRequired: "Tax amount is required",
    taxNonNegative: "Tax amount must be 0 or more",
    taxLessThanAmount: "Tax amount cannot be more than amount",

    typeRequired: "Transaction type is required",
  },
} as const;
