import { useEffect, useRef, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Navbar from "./Navbar";
import {
  Transaction,
  getTransactions,
  downloadAllTransactions,extractApiError
} from "../service/TransactionService";
import "../assets/Transactions.css";
import "../assets/Auth.css";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";
import { toast } from "react-toastify";
import axios from "axios";

const Transactions = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  // ================= FILTERS =================
  const [financialYear, setFinancialYear] = useState("");
  const [month, setMonth] = useState<number | undefined>(undefined);
  const [allMonths, setAllMonths] = useState(true);

  const [organizationFilter, setOrganizationFilter] = useState("");
  // ================= DATA =================
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(false);

  // ================= PAGINATION =================
  const [pageNumber, setPageNumber] = useState(0); // 0-based
  const [pageSize, setPageSize] = useState(10);
  const [totalRecords, setTotalRecords] = useState(0);

  // ================= DOWNLOAD =================
  const [showDownloadMenu, setShowDownloadMenu] = useState(false);
  const [showDownloadAllMenu, setShowDownloadAllMenu] = useState(false);

  const downloadRef = useRef<HTMLDivElement>(null);
  const downloadAllRef = useRef<HTMLDivElement>(null);

  const triggerDownload = (url: string, fileName: string) => {
    const link = document.createElement("a");
    link.href = url;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(url);
  };

  //=============Filtered transactions=============
  const filteredTransactions = transactions.filter(t =>
    t.organizationName
      ?.toLowerCase()
      .includes(organizationFilter.toLowerCase())
  );



  // ================= FETCH TRANSACTIONS =================
  useEffect(() => {   
    fetchTransactions();
  }, [pageNumber, pageSize, financialYear, month, allMonths]);

const fetchTransactions = async () => {
  setLoading(true);
  try {
    const res = await getTransactions({
      pageNumber,
      pageSize,
      financialYear: financialYear || undefined,
      month: allMonths ? undefined : month
    });

    setTransactions(res.data.transactions);
    setTotalRecords(res.data.totalRecords);

  } catch (err) {

  // 🔌 Server unreachable / network error
  if (!axios.isAxiosError(err) || !err.response) {
    if(!toast.isActive("server-down")){
      toast.error("Server is currently unavailable. Please try again later");
    }
    setTransactions([]);
    setTotalRecords(0);
    return;
  }

  // 🔐 Unauthorized / token expired
  if (err.response.status === 401) {
    localStorage.removeItem("accessToken");
    navigate("/login");
    return;
  }

  // ❗ All other backend errors (400, 500, etc.)
  toast.error(extractApiError(err),{
      toastId:"api-error"
    });

  setTransactions([]);
  setTotalRecords(0);
}
finally {
    setLoading(false);
  }
};


  const fetchAllTransactionsFromDB = async (): Promise<Transaction[]> => {
    try {
      const res = await downloadAllTransactions();
      return res.data;
    }catch (err) {

  // 🔌 Server unreachable / network issue
  if (!axios.isAxiosError(err) || !err.response) {
    if(!toast.isActive("server-down")){
      toast.error("Server is currently unavailable. Please try again later");
    }
    
    return [];
  }

  // 🔐 Unauthorized / token expired
  if (err.response.status === 401) {
    localStorage.removeItem("accessToken");
    navigate("/login");
    return [];
  }

  // ❗ Backend handled exceptions (400, 500 etc from Spring Boot)
  const apiError = err.response.data;

  if (apiError?.errorMessage) {
    toast.error(apiError.errorMessage);   // comes from ErrorInfo in backend
  } else {
    toast.error("Something went wrong while loading transactions");
  }

  return [];
}

  };

  //===================Dowload Helpers ==============

  //json
  const downloadCurrentJSON = () => {
    if (!transactions.length) return alert("No transactions");

    const blob = new Blob([JSON.stringify(transactions, null, 2)], {
      type: "application/json"
    });

    const url = URL.createObjectURL(blob);
    triggerDownload(url, "transactions.json");
  };

  const downloadAllJSON = async () => {
    const data = await fetchAllTransactionsFromDB();
    if (!data.length) return alert("No transactions");

    const blob = new Blob([JSON.stringify(data, null, 2)], {
      type: "application/json"
    });

    const url = URL.createObjectURL(blob);
    triggerDownload(url, "all-transactions.json");
  };

  //csv
  const toCSV = (data: Transaction[]) => {
    const headers = ["Date", "Organization", "Amount", "Tax", "Type"];
    const rows = data.map(t =>
      [t.date, t.organizationName, t.amount, t.taxAmount, t.type].join(",")
    );
    return [headers.join(","), ...rows].join("\n");
  };

  const downloadCurrentCSV = () => {
    if (!transactions.length) return alert("No transactions");

    const blob = new Blob([toCSV(transactions)], { type: "text/csv" });
    const url = URL.createObjectURL(blob);
    triggerDownload(url, "transactions.csv");
  };

  const downloadAllCSV = async () => {
    const data = await fetchAllTransactionsFromDB();
    if (!data.length) return alert("No transactions");

    const blob = new Blob([toCSV(data)], { type: "text/csv" });
    const url = URL.createObjectURL(blob);
    triggerDownload(url, "all-transactions.csv");
  };

  //pdf
  const downloadCurrentPDF = () => {
    if (!transactions.length) {
      alert("No transactions");
      return;
    }

    const doc = new jsPDF();

    doc.text("Transactions (Current Page)", 14, 15);

    autoTable(doc, {
      startY: 20,
      head: [["S.No", "Date", "Organization", "Amount", "Tax", "Type"]],
      body: transactions.map((t, index) => [
        index + 1,
        t.date,
        t.organizationName,
        t.amount,
        t.taxAmount,
        t.type
      ])
    });

    doc.save("transactions.pdf");
  };
  const downloadAllPDF = async () => {
    const data = await fetchAllTransactionsFromDB();

    if (!data.length) {
      alert("No transactions");
      return;
    }

    const doc = new jsPDF();
    doc.text("All Transactions", 14, 15);

    autoTable(doc, {
      startY: 20,
      head: [["S.No", "Date", "Organization", "Amount", "Tax", "Type"]],
      body: data.map((t, index) => [
        index + 1,
        t.date,
        t.organizationName,
        `${t.amount}.00`,
        `${t.taxAmount}.00`,
        t.type
      ])
    });

    doc.save("all-transactions.pdf");
  };


  // ================= CLICK OUTSIDE =================
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        downloadRef.current &&
        !downloadRef.current.contains(event.target as Node)
      ) {
        setShowDownloadMenu(false);
      }
      if (
        downloadAllRef.current &&
        !downloadAllRef.current.contains(event.target as Node)
      ) {
        setShowDownloadAllMenu(false);
      }

    };
    document.addEventListener("mousedown", handleClickOutside);
    return () =>
      document.removeEventListener("mousedown", handleClickOutside);
  }, []);
  

  //Get pages
  const getPageNumbers = () => {
    const pages: number[] = [];
    const start = Math.max(0, pageNumber - 2);
    const end = Math.min(totalPages - 1, pageNumber + 2);

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    return pages;
  };


  const totalPages = Math.ceil(totalRecords / pageSize);

  return (
    <>
      <Navbar onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} />

      <div
        className={`app-layout ${sidebarOpen ? "sidebar-open" : "sidebar-closed"
          }`}
      >
        <aside className={`sidebar ${sidebarOpen ? "open" : "closed"}`}>
          <button

  type="button"

  className={`sidebar-item ${location.pathname === "/login-success" ? "active" : ""}`}

  onClick={() => navigate("/login-success")}

>

  🏠 Dashboard

</button>

          <div
            className={`sidebar-item ${location.pathname === "/transactions" ? "active" : ""
              }`}
          >
            📊 Transactions
          </div>

          <button

  type="button"

  className={`sidebar-item ${location.pathname === "/form-90c" ? "active" : ""}`}

  onClick={() => navigate("/form-90c")}

>

  📝 Form 90C

</button>
        </aside>

        <main className="dashboard-page">
          <div className="transactions-container rounded-4">
            <div className="header-wrapper mb-3">

              {/* ===== Row 1 : Title + Buttons ===== */}
              <div className="header-top">
                <h2>Transactions Dashboard</h2>

                <div className="d-flex gap-2">

                  <div className="position-relative" ref={downloadRef}>
                    <button
                      className="btn btn-outline-success"
                      onClick={() => setShowDownloadMenu(p => !p)}
                    >
                      Download
                    </button>

                    {showDownloadMenu && (
                      <div className="dropdown-menu show">
                        <button className="dropdown-item" onClick={downloadCurrentJSON}>JSON</button>
                        <button className="dropdown-item" onClick={downloadCurrentCSV}>CSV</button>
                        <button className="dropdown-item" onClick={downloadCurrentPDF}>PDF</button>
                      </div>
                    )}
                  </div>

                  <div className="position-relative" ref={downloadAllRef}>
                    <button
                      className="btn btn-outline-primary"
                      onClick={() => setShowDownloadAllMenu(p => !p)}
                    >
                      Download All
                    </button>

                    {showDownloadAllMenu && (
                      <div className="dropdown-menu show">
                        <button className="dropdown-item" onClick={downloadAllJSON}>JSON</button>
                        <button className="dropdown-item" onClick={downloadAllCSV}>CSV</button>
                        <button className="dropdown-item" onClick={downloadAllPDF}>PDF</button>
                      </div>
                    )}
                  </div>

                </div>
              </div>


              {/* ===== Row 2 : Filters ===== */}
              <div className="filter-rows">

                <div className="filter-group">
                  <label htmlFor="records">Records</label>
                  <select
                    className="form-select"
                    value={pageSize}
                    onChange={e => {
                      setPageSize(Number(e.target.value));
                      setPageNumber(0);
                    }}
                  >
                    <option value={5}>5</option>
                    <option value={10}>10</option>
                    <option value={20}>20</option>
                  </select>
                </div>
                <div className="filter-group">
                  <label htmlFor="organization">Organization</label>
                  <input
                    type="text"
                    className="form-select"
                    placeholder="Search organization"
                    value={organizationFilter}
                    onChange={e => {
                      setOrganizationFilter(e.target.value);
                      setPageNumber(0);
                    }}
                  />
                </div>
                
                <div className="filter-group">
                  <label htmlFor="financialYear">Financial Year</label>
                  <select
                    className="form-select"
                    value={financialYear}
                    onChange={e => {
                      setFinancialYear(e.target.value);
                      setPageNumber(0);
                    }}
                  >
                    <option value="">All Years</option>
                    <option value="2023-2024">2023-24</option>
                    <option value="2024-2025">2024-25</option>
                  </select>
                </div>

                <div className="filter-group">
                  <label htmlFor="month">Month</label>
                  <select
                    className="form-select"
                    disabled={allMonths}
                    value={month ?? ""}
                    onChange={e => {
                      setMonth(Number(e.target.value));
                      setAllMonths(false);
                      setPageNumber(0);
                    }}
                  >
                    <option value="" disabled>Select Month</option>
                    <option value={1}>January</option>
                    <option value={2}>February</option>
                    <option value={3}>March</option>
                    <option value={4}>April</option>
                    <option value={5}>May</option>
                    <option value={6}>June</option>
                    <option value={7}>July</option>
                    <option value={8}>August</option>
                    <option value={9}>September</option>
                    <option value={10}>October</option>
                    <option value={11}>November</option>
                    <option value={12}>December</option>
                  </select>
                </div>

                <div className="filter-group checkbox-group">
                  <label htmlFor="allMonths">All Months</label>
                  <input
                  
                    type="checkbox"
                    checked={allMonths}
                    onChange={e => {
                      setAllMonths(e.target.checked);
                      setMonth(undefined);
                      setPageNumber(0);
                    }}
                  />
                </div>

              </div>

            </div>

              <div className="summary-cards">
                  <div className="summary-card">
                    <p className="text-dark">Total Records</p>
                    <h4>{transactions.length}</h4>
                  </div>

                  <div className="summary-card">
                    <p className="text-dark">Total Amount</p>
                    <h4>
                      ₹ {transactions.reduce((a,t)=>a+t.amount,0)}
                    </h4>
                  </div>

                  <div className="summary-card">
                    <p className="text-dark">Total Tax</p>
                    <h4>
                      ₹ {transactions.reduce((a,t)=>a+t.taxAmount,0)}
                    </h4>
                  </div>
                </div>
            {/* ================= TABLE ================= */}
            <div className="table-responsive rounded overflow-hidden shadow">
              <table className="table table-bordered table-striped mb-0">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Organization</th>
                    <th>Amount</th>
                    <th>Tax</th>
                    <th>Type</th>
                  </tr>
                </thead>
                <tbody>
  {loading ? (
    <>
      {[1,2,3,4,5].map(i => (
        <tr key={i}>
          <td><div className="skeleton medium"></div></td>
          <td><div className="skeleton large"></div></td>
          <td><div className="skeleton small"></div></td>
          <td><div className="skeleton small"></div></td>
          <td><div className="skeleton medium"></div></td>
        </tr>
      ))}
    </>
  ) : filteredTransactions.length === 0 ? (
    <tr>
      <td colSpan={5} className="text-center">
        No transactions found
      </td>
    </tr>
  ) : (
    filteredTransactions.map(t => (
      <tr key={t.txnId}>
        <td>{t.date}</td>
        <td>{t.organizationName}</td>
        <td>{t.amount}</td>
        <td>{t.taxAmount}</td>
        <td>
          <span className={`badge ${t.type}`}>{t.type}</span>
        </td>
      </tr>
    ))
  )}
</tbody>
              </table>
            </div>

            {/* ================= PAGINATION ================= */}
            {/* ================= PAGINATION ================= */}
            <div className="d-flex justify-content-center align-items-center mt-3 gap-1">

              {/* Prev */}
              <button
                className="btn btn-outline-secondary"
                disabled={pageNumber === 0}
                onClick={() => setPageNumber(p => p - 1)}
              >
                Prev
              </button>

              {/* Page Jump Buttons */}
              {getPageNumbers().map(p => (
                <button
                  key={p}
                  className={`btn ${p === pageNumber
                    ? "btn-primary"
                    : "btn-outline-secondary"
                    }`}
                  onClick={() => setPageNumber(p)}
                >
                  {p + 1}
                </button>
              ))}

              {/* Next */}
              <button
                className="btn btn-outline-secondary"
                disabled={pageNumber >= totalPages - 1}
                onClick={() => setPageNumber(p => p + 1)}
              >
                Next
              </button>

            </div>

          </div>
        </main>
      </div>
    </>
  );
};

export default Transactions;
