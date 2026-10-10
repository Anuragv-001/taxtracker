
import { useState } from "react";
import { useNavigate} from "react-router-dom";
import { Messages } from "../messages/Messages";
import LoginRegisterNavbar from "./LoginRegisterNavbar";
import { isValidEmail, isValidPassword } from "../validators/Validations";
import { toast } from "react-toastify";
import "../assets/LoginRegister.css"
import { loginUser } from "../service/LoginService";

const Login = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: "",
    password: ""
  });

  const [formErrors,setFormErrors]=useState({
    email:"",
    password:""
  })
  const [error, setError] = useState("");

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const {name,value}=e.target;
    setFormData(prev=>({...prev,[name]:value}));
    validateField(name,value);

  };

  const isFormValid=()=>{
    const noErrors=Object.values(formErrors).every(err=>err==="");
    const allFilled=Object.values(formData).every(val=>val!=="");
    return noErrors && allFilled;
  }
  const validateField=(name:string,value:string)=>{
    let errors={...formErrors};
    switch(name)
    {
      case "email":
        errors.email=isValidEmail(value)?"":Messages.INVALID_EMAIL
        break;
      case "password":
        errors.password=isValidPassword(value)?"":Messages.INVALID_PASSWORD
        break;  
    }
    setFormErrors(errors);
  }

  const formValid=isValidEmail(formData.email) && isValidPassword(formData.password);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    const { email, password } = formData;

    
    
    try {
      

      if(!isFormValid()){
        setError(Messages.REQUIRED_FIELDS);
        return;
      }
      
      setError("");
      

      
      const res=await loginUser(email,password);
      toast.success("Logged in Successfully")
      setTimeout(()=>{
      navigate("/login-success",{
        state:{
          name:res.userName
        }
      });
    },700)

   
      

    } catch (err:any) {
      setError(err.response.data.errorMessage);
    }
  };

  return (
    <>
    <LoginRegisterNavbar/>
    <div className="auth-page">
      <div className="auth-card">
        <h3 className="auth-title">{"\u{1F512}"}Login</h3>

        {error && (
          <div className="alert alert-danger text-center">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin}>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">{"\u{1F4E7}"}Email</label>
            <input
              type="email"
              className="form-control"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter email"
            />
          </div>

          {formErrors.email &&(<small className="text-danger">{formErrors.email}</small>)}

          <div className="mb-3">
            <label htmlFor="password" className="form-label">{"\u{1F511}"}Password</label>
            <input
              type="password"
              className="form-control"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter password"
            />
          </div>
          {formErrors.password &&(<small className="text-danger">{formErrors.password}</small>)}

          <button type="submit" className="btn btn-warning w-100" disabled={!formValid}>
            Login
          </button>
        </form>

        <div className="auth-footer" role="button" tabIndex={0}>
          New user?{" "}
          <span
            className="auth-link" role="button" tabIndex={0}
            onClick={() => navigate("/register")}
            onKeyDown={(e)=>{
              if(e.key==="Enter" || e.key===" "){
                navigate("/register")
              }
            }}
          >
            Register
          </span>
        </div>
      </div>
    </div>
    </>
  );
};

export default Login;
