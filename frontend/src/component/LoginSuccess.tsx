import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "../assets/Auth.css";
import Navbar from "./Navbar";

const LoginSuccess = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const user = localStorage.getItem("userName")

  

  return (
    <>
      
      <Navbar onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} />

      
      <div
        className={`app-layout ${
          sidebarOpen ? "sidebar-open" : "sidebar-closed"
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
            className={`sidebar-item ${
              location.pathname === "/transactions" ? "active" : ""
            }`}
            onClick={() => navigate("/transactions")}
             role="button" tabIndex={0}
              onKeyDown={(e)=>{
              if(e.key==="Enter" || e.key===" ")
              {
                navigate("/transactions")
              }
            }}
          >
            📊 <span>Transactions</span>
          </div>

          <div
            className={`sidebar-item ${
              location.pathname === "/form-90c" ? "active" : ""
            }`}
            onClick={() => navigate("/form-90c")}
            role="button" tabIndex={0}
           onKeyDown={(e)=>{
              if(e.key==="Enter" || e.key===" ")
              {
                navigate("/form-90c")
              }
            }}
          >
            📝 <span>Form 90C</span>
          </div>
        </aside>

        
        <main className="dashboard-page">
          <div className="dashboard-container">
            <h2 className="dashboard-title">Welcome, {user}</h2>
            <p className="dashboard-subtitle">
              Manage your tax filings and transactions
            </p>

           <div className="dashboard-grid">

  

  <div className="dashboard-card">

    <div className="card-icon">📊</div>

    <h4>Transactions</h4>

    <p>

      View and analyze all your tax-related transactions including

      TDS, TCS, and GST in one place.

    </p>

    <button

      className="btn btn-warning w-100 mt-2"

      onClick={() => navigate("/transactions")}

    >

      View Transactions

    </button>

  </div>

 
  <div className="dashboard-card">

    <div className="card-icon">📝</div>

    <h4>Form 90C</h4>

    <p>

      Fill, upload documents, and submit Form 90C to claim

      eligible tax deductions smoothly.

    </p>

    <button

      className="btn btn-warning w-100 mt-2"

      onClick={() => navigate("/form-90c")}

    >

      Fill Form 90C

    </button>

  </div>

</div>



          </div>
        </main>
      </div>
    </>
  );
};

export default LoginSuccess;
