import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Navbar from "./Navbar";
import { Form90CTransaction } from "../model/Form90C";
import {
  saveForm90C,
  getSavedForm90C,
} from "../service/Form90CService";
import {
  validateFullName,
  validateMobile,
  validateFinancialYear,
  validateTransactionRow
} from "../validators/Form90CValidation";
import { toast } from "react-toastify";
import FormErrors from "../model/FormErrors";
import "../assets/Auth.css";

export default function Form90CPart1() {
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const [name, setName] = useState("");
  const [mobile, setMobile] = useState("");
  const [financialYear, setFinancialYear] = useState("");

  const [transactions, setTransactions] = useState<Form90CTransaction[]>([
    {
      organizationName: "",
      amount: "",
      taxAmount: "",
      type: "",
      txnDate: ""
    }
  ]);

  const txnTypes: string[] = ["TCS", "TDS", "GST"];

  const [errors, setErrors] = useState<FormErrors>({});
  const [isDraftValid, setIsDraftValid] = useState(false);
  const [isFormCompleteAndValid, setIsFormCompleteAndValid] = useState(false);

  /* ================= LOAD SAVED FORM ================= */
  useEffect(() => {
    const loadForm = async () => {
      const data = await getSavedForm90C();
      if (!data) return;

      setName(data.name ?? "");
      setMobile(data.mobileNumber ?? "");
      setFinancialYear(data.financialYear ?? "");
      if (data.transactionHistory?.length) {
        setTransactions(data.transactionHistory);
      }
    };
    loadForm();
  }, []);

  /* ================= LIVE VALIDATION ================= */
  useEffect(() => {
    const newErrors: FormErrors = {};

    newErrors.name = name.trim() ? validateFullName(name) : "";
    newErrors.mobileNumber = mobile.trim() ? validateMobile(mobile) : "";
    newErrors.financialYear = financialYear.trim()
      ? validateFinancialYear(financialYear)
      : "";

    newErrors.transactions = transactions.map(tx => {
      const isEmptyRow =
        tx.txnDate === "" &&
        tx.organizationName.trim() === "" &&
        (tx.amount === "" || tx.amount === 0) &&
        (tx.taxAmount === "" || tx.taxAmount === 0) &&
        tx.type === "";

      return isEmptyRow ? {} : validateTransactionRow(tx, financialYear);
    });

    setErrors(newErrors);

    const hasAnyValue =
      name.trim() !== "" ||
      mobile.trim() !== "" ||
      financialYear.trim() !== "" ||
      transactions.some(tx =>
        tx.txnDate !== "" ||
        tx.organizationName.trim() !== "" ||
        tx.amount !== "" ||
        tx.taxAmount !== "" ||
        tx.type !== ""
      );

    const hasAnyErrors =
      !!newErrors.name ||
      !!newErrors.mobileNumber ||
      !!newErrors.financialYear ||
      newErrors.transactions.some(txErr =>
        Object.values(txErr).some(Boolean)
      );

    const isComplete =
      name.trim() !== "" &&
      mobile.trim() !== "" &&
      financialYear.trim() !== "" &&
      transactions.every(tx =>
        tx.txnDate !== "" &&
        tx.organizationName.trim() !== "" &&
        tx.amount !== "" &&
        tx.taxAmount !== "" &&
        tx.type !== ""
      );

    setIsDraftValid(hasAnyValue && !hasAnyErrors);
    setIsFormCompleteAndValid(isComplete && !hasAnyErrors);
  }, [name, mobile, financialYear, transactions]);

  /* ================= HANDLERS ================= */
  const handleChange = (
    index: number,
    field: keyof Form90CTransaction,
    value: any
  ) => {
    const updated = [...transactions];
    updated[index] = { ...updated[index], [field]: value };
    setTransactions(updated);
  };

  const addTransaction = () => {
    setTransactions([
      ...transactions,
      { txnDate: "", organizationName: "", amount: "", taxAmount: "", type: "" }
    ]);
  };

  const removeTransaction = (index: number) => {
    if (transactions.length === 1) {
      toast.error("At least one transaction is required");
      return;
    }
    setTransactions(transactions.filter((_, i) => i !== index));
  };

  const handleSave = async () => {
    const res = await saveForm90C({
      name,
      mobileNumber: mobile,
      financialYear,
      transactionHistory: transactions
    });
    localStorage.setItem("formId", res.data.toString());
    toast.info("Form saved successfully");
  };

  const handleReset = () => {
    if (!globalThis.confirm("Are you sure you want to reset the form?")) return;
    setName("");
    setMobile("");
    setFinancialYear("");
    setTransactions([
      { txnDate: "", organizationName: "", amount: "", taxAmount: "", type: "" }
    ]);
    setErrors({});
  };

  const handleNext = () => {
    if (!isFormCompleteAndValid) return;
    handleSave();
    navigate("/form-90c/upload");
  };

  /* ================= UI ================= */
  return (
    <>
      <Navbar onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} />

      <div className={`app-layout ${sidebarOpen ? "sidebar-open" : "sidebar-closed"}`}>
        <aside className={`sidebar ${sidebarOpen ? "open" : "closed"}`}>
          <button
            type="button"
            className={`sidebar-item ${location.pathname === "/login-success" ? "active" : ""}`}
            onClick={() => navigate("/login-success")}
          >
            🏠 Dashboard
          </button>

          <button
            type="button"
            className={`sidebar-item ${location.pathname === "/login-success" ? "active" : ""}`}
            onClick={() => navigate("/transactions")}
          >
            📊 Transactions
          </button>

          <div className={`sidebar-item ${location.pathname.startsWith("/form-90c") ? "active" : ""}`}>
            📝 Form 90C
          </div>
        </aside>

        <main className="dashboard-page">
          <div className="container mt-4">
            <div className="card shadow-sm border">
              <div className="card-body form-90c">

                <h4 className="text-center mb-4 fs-3 fw-bold">FORM 90C</h4>

                {/* USER DETAILS */}
                <div className="mb-4 p-3 border rounded">
                  <h5 className="text-center fw-bold mb-3 fs-3">User Details</h5>

                  <div className="row mb-2">
                    <div className="col-md-4">
                      <label htmlFor="name" className="form-label fw-bold fs-5">Full Name</label>
                      <input
                        className={`form-control ${name.trim() !== "" && errors.name ? "is-invalid" : ""}`}
                        value={name}
                        onChange={e => setName(e.target.value)}
                      />
                      {name.trim() !== "" && errors.name && (
                        <div className="invalid-feedback">{errors.name}</div>
                      )}
                    </div>

                    <div className="col-md-4">
                      <label htmlFor="number" className="form-label fw-bold fs-5">Mobile Number</label>
                      <input
                        type="number"
                        className={`form-control ${mobile.trim() !== "" && errors.mobileNumber ? "is-invalid" : ""}`}
                        value={mobile}
                        onChange={e => setMobile(e.target.value)}
                      />
                      {mobile.trim() !== "" && errors.mobileNumber && (
                        <div className="invalid-feedback">{errors.mobileNumber}</div>
                      )}
                    </div>

                    <div className="col-md-4">
                      <label htmlFor="financialYear" className="form-label fw-bold fs-5">Financial Year</label>
                      <input
                        className={`form-control ${financialYear.trim() !== "" && errors.financialYear ? "is-invalid" : ""}`}
                        placeholder="YYYY-YYYY"
                        value={financialYear}
                        onChange={e => setFinancialYear(e.target.value)}
                      />
                      {financialYear.trim() !== "" && errors.financialYear && (
                        <div className="invalid-feedback">{errors.financialYear}</div>
                      )}
                    </div>
                  </div>
                </div>

                {/* TRANSACTION HISTORY */}
                <h6 className="mt-4 mb-2 fw-bold">Transaction History</h6>

                <div className="table-responsive border rounded">
                  <table className="table table-bordered table-sm align-middle">
                    <thead className="table-light">
                      <tr>
                        <th>Date</th>
                        <th>Organization</th>
                        <th>Amount</th>
                        <th>Tax Amount</th>
                        <th>Type</th>
                        <th>Action</th>
                      </tr>
                    </thead>

                    <tbody>
                      {transactions.map((tx, i) => (
                        <tr key={i}>
                          <td>
                            <input
                              type="date"
                              max="9999-12-31"
                              className={`form-control form-control-sm ${
                                errors.transactions?.[i]?.transactionDate?"is-invalid":""
                              }`} 
                              value={tx.txnDate}
                              onChange={e => handleChange(i, "txnDate", e.target.value)}
                            />
                         
                            {errors.transactions?.[i]?.transactionDate && (
                              <div className="text-danger invalid-feedback d-block">{errors.transactions[i].transactionDate}</div>
                            )}
                             </td>
                          <td>
                            <input
                              className={`form-control form-control-sm ${
                                errors.transactions?.[i]?.organizationName?"is-invalid":""
                              }`}
                              value={tx.organizationName}
                              onChange={e => handleChange(i, "organizationName", e.target.value)}
                            />
                            {errors.transactions?.[i]?.organizationName && (
                              <div className="text-danger invalid-feedback d-block">{errors.transactions[i].organizationName}</div>
                            )}
                          </td>
                            
                          <td>
                            <input
                              type="number"
                              className={`form-control form-control-sm ${
                                errors.transactions?.[i]?.amount?"is-invalid":""
                              }`}
                              value={tx.amount}
                              onChange={e => handleChange(i, "amount", e.target.value)}
                            />
                            {errors.transactions?.[i]?.amount && (
                              <div className="text-danger invalid-feedback d-block">{errors.transactions[i].amount}</div>
                            )}
                          </td>

                          <td>
                            <input
                              type="number"
                              className={`form-control form-control-sm ${
                                errors.transactions?.[i]?.taxAmount?"is-invalid":""
                              }`}
                              value={tx.taxAmount}
                              onChange={e => handleChange(i, "taxAmount", e.target.value)}
                            />
                            {errors.transactions?.[i]?.taxAmount && (
                              <div className="text-danger invalid-feedback d-block">{errors.transactions[i].taxAmount}</div>
                            )}
                          </td>

                          <td>
                            <select
                              className="form-control form-control-sm"
                              value={tx.type}
                              onChange={e => handleChange(i, "type", e.target.value)}
                            >
                              <option value="">--Select Type--</option>
                              {txnTypes.map(type => (
                                <option key={type} value={type}>{type}</option>
                              ))}
                            </select>
                          </td>

                          <td className="text-center">
                            <button className="btn btn-danger btn-sm" onClick={() => removeTransaction(i)}>
                              Remove
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                <button className="btn btn-secondary btn-sm my-3" onClick={addTransaction}>
                  + Add Transaction
                </button>

                <div className="d-flex gap-2">
                  <button className="btn btn-warning btn-sm" onClick={handleSave} disabled={!isDraftValid}>
                    Save Draft
                  </button>

                  <button className="btn btn-secondary btn-sm" onClick={handleReset}>
                    Reset
                  </button>

                  <button className="btn btn-primary btn-sm" onClick={handleNext} disabled={!isFormCompleteAndValid}>
                    Next
                  </button>
                </div>

              </div>
            </div>
          </div>
        </main>
      </div>
    </>
  );
}