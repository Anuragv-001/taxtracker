import "../assets/LearnMore.css";
import LoginRegisterNavbar from "./LoginRegisterNavbar";

const LearnMore = () => {

  return (

    <>
    <LoginRegisterNavbar/>
    <div className="learnmore-wrapper">

      <div className="learnmore-container">

        <h1 className="learnmore-title">About Income Tax & TaxTracker</h1>

        <p className="learnmore-subtitle">

          Learn the basics of income tax, Form 90C, and how TaxTracker simplifies

          tax management.

        </p>

        <div className="learnmore-section">

          <h3>What is Income Tax?</h3>

          <p>

            Income tax is a tax levied by the government on the income earned by

            individuals and organizations during a financial year. It is one of

            the primary sources of revenue for the government and helps fund

            public services and infrastructure.

          </p>

        </div>

        <div className="learnmore-section">

          <h3>Understanding Form 90C</h3>

          <p>

            Form 90C is used to claim deductions under Section 80C of the Income

            Tax Act. Investments such as Public Provident Fund (PPF), Employee

            Provident Fund (EPF), ELSS mutual funds, life insurance premiums, and

            fixed deposits are eligible under this section.

          </p>

        </div>

        <div className="learnmore-section">

          <h3>Why Tax Planning Matters</h3>

          <p>

            Proper tax planning helps reduce tax liability, ensures legal

            compliance, avoids penalties, and promotes better financial

            decision-making throughout the year.

          </p>

        </div>

        <div className="learnmore-section">

          <h3>How TaxTracker Helps You</h3>

          <ul>

            <li>Centralized tracking of tax-related transactions</li>

            <li>Secure upload and submission of Form 90C</li>

            <li>Downloadable transaction reports</li>

            <li>User-friendly dashboard for better insights</li>

          </ul>

        </div>

        <div className="learnmore-footer">

          <p>

            TaxTracker is designed to make income tax filing simpler, more

            transparent, and stress-free for users.

          </p>

        </div>

      </div>

    </div>
    </>

  );

};

export default LearnMore;


