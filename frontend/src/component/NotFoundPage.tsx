import { useNavigate } from "react-router-dom";

import "../assets/NotFound.css";

const NotFoundPage = () => {

  const navigate = useNavigate();

  return (

    <div className="notfound-container">

      <h1 className="notfound-code">404</h1>

      <h2 className="notfound-title">Page Not Found</h2>

      <p className="notfound-text">

        The page you’re looking for doesn’t exist or may have been moved.

      </p>

      <p className="notfound-subtext">

        Please check the URL, or return to the dashboard to continue managing your tax filings.

      </p>

      <div className="notfound-actions">

        

        <button

          className="btn btn-outline-secondary"

          onClick={() => navigate("/")}

        >

          Home

        </button>

      </div>

    </div>

  );

};

export default NotFoundPage;


