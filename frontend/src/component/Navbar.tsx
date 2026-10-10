import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "../assets/Auth.css";
import { useState } from "react";
import { logout } from "../service/LogoutService";

interface NavbarProps {
  onToggleSidebar: () => void;
}

const Navbar = ({ onToggleSidebar }: NavbarProps) => {
  const navigate = useNavigate();
  const userName=localStorage.getItem("userName");
  const userMail=localStorage.getItem("userEmail");
  const [showLogoutConfirm,setShowLogoutConfirm]=useState(false);

  const handleLogout =async () => {
    try{
     await logout();
    }catch(error:any){
      toast.info(error.respose?.data);
    }
    finally{
        localStorage.removeItem("accessToken");
        localStorage.removeItem("userName"); 
        localStorage.removeItem("userEmail");  
        localStorage.removeItem("formId");
        toast.success("Logged out Successfully");
        setTimeout(() => navigate("/"), 700);
    }
    
  };

  return (
    <nav className="dashboard-navbar">
      {/* LEFT */}
      <div className="navbar-left">
        <span className="hamburger" onClick={onToggleSidebar} role="button" tabIndex={0} onKeyDown={(e)=>{
          if(e.key==="Enter" || e.key===" ")
          {
            onToggleSidebar()
          }
        }}>
          ☰
        </span>

        <Link to="/login-success" className="brand-link">
          TaxTracker
        </Link>
      </div>

      {/* RIGHT */}
      <div className="navbar-right">
        <div className="profile-wrapper">
          <span className="profile-icon">{"\u{1F464}"}</span>
          <div className="profile-tooltip">
            <p className="profile-name">{userName}</p>
            <p className="profile-email">{userMail}</p>
          </div>
        </div>
        
        <button className="btn btn-outline-warning" onClick={()=>setShowLogoutConfirm(true)} >
          Logout
        </button>
        </div>
      {showLogoutConfirm && (

  <div className="logout-overlay">

    <div className="logout-card">

      <h4>Confirm Logout</h4>

      <p>Are you sure you want to logout?</p>

      <div className="logout-actions">

        <button

          className="btn btn-secondary"

          onClick={() => setShowLogoutConfirm(false)}
          

        >

          Cancel

        </button>

        <button

          className="btn btn-danger"

          onClick={handleLogout}

        >

          Yes, Logout

        </button>

      </div>

    </div>

  </div>

)}



      </nav>
  );
};

export default Navbar;
