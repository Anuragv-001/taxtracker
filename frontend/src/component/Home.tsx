import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./Home.css"

const Home = () => {
  const navigate = useNavigate();

  return (
    <>
      
       <nav
        className="navbar px-4"
        style={{ background: "rgba(15, 23, 42, 0.9)" }}
      >
        <span className="navbar-brand fw-bold text-white">
          TaxTracker
        </span>
        <div className="ms-auto">
          <button
            className="btn btn-outline-light me-2"
            onClick={() => navigate("/login")}
          >
            Login
          </button>
          <button
            className="btn btn-warning"
            onClick={() => navigate("/register")}
          >
            Register
          </button>
        </div>
      </nav> 

      
      <section className="py-5 text-center">
        <div className="container">
          <h1 className="fw-bold display-5">
            Simplify Your Income Tax Filing
          </h1>
          <p className="lead mt-3">
            Manage transactions, download reports, and submit Form 90C — all in
            one place.
          </p>

          <div className="mt-4">
            <button
              className="btn btn-primary btn-lg me-3"
              onClick={() => navigate("/register")}
            >
              Get Started
            </button>
            <button className="btn btn-outline-light btn-lg" onClick={()=>navigate("/learn-more")}>
              Learn More
            </button>
          </div>
        </div>
      </section>

     
      <section className="py-5">
        <div className="container">
          <div className="row text-center" color="grey">
            {[
              ["Track Your Taxes", "View and manage your yearly tax transactions including TDS,TCS, and deductions in one place."],
              ["Download Reports", "Download your transaction reports in JSON or other supported formats for easy tax filing."],
              ["Transactions", "View all tax-related transactions with support of financial year checks and accurate record keeping."],
              ["Form 90C Submission", "Fill and Submit From 90C online with secure document upload and validation."],
            ].map(([title, text], i) => (
              <div key={title} className="col-md-3 mb-4">
                <div className="card h-100 shadow">
                  <div className="card-body">
                    <h5 className="fw-bold">{title}</h5>
                    <p>{text}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>
    </>
  );
};

export default Home;
