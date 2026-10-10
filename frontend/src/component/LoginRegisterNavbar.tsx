import { useNavigate } from "react-router-dom";
import "../assets/LoginRegNavbar.css"
const LoginRegisterNavbar=()=>{
    const navigate=useNavigate();

    return(
        <nav className="auth-navbar">
            <div className="auth-logo" onClick={()=>navigate("/") } role="button"
             tabIndex={0} onKeyDown={(e)=>{
                if(e.key==="Enter" || e.key===" ")
                {
                    navigate("/")
                }
            }}>Tax Tracker</div>
        </nav>
    )
}


export default LoginRegisterNavbar;