import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./component/Home";
import Login from "./component/Login"
import Register from "./component/Register";
import LoginSuccess from "./component/LoginSuccess";
import Transactions from "./component/Transactions";
import Form90CPart1 from "./component/Form90CPart1";
import Form90CPart2 from "./component/Form90CPart2"
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css"
import Footer from "./component/Footer";
import ProtectedRoutes from "./component/ProtectedRoutes";
import LearnMore from "./component/LearnMore";
import NotFoundPage from "./component/NotFoundPage";


function App() {
  return (
    
    <BrowserRouter>
    <div className="app-root">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/learn-more" element={<LearnMore/>} ></Route>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register/>}></Route>
        <Route path="/login-success" element={
          <ProtectedRoutes><LoginSuccess /></ProtectedRoutes>} />
        <Route path="/transactions" element={<ProtectedRoutes><Transactions/></ProtectedRoutes>}/>
         <Route path = "/form-90c" element={<ProtectedRoutes><Form90CPart1/></ProtectedRoutes>}/>
        <Route path = "/form-90c/upload" element={<ProtectedRoutes><Form90CPart2/></ProtectedRoutes>}/>
        <Route path="*" element={<NotFoundPage/>}></Route>
      </Routes>
      <ToastContainer
        position="top-right"
        autoClose={2500}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        pauseOnHover
        theme="colored"
    />
    <Footer/>
    </div>
    </BrowserRouter>
    
  );
}

export default App;
