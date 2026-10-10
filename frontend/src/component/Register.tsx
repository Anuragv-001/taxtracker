import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../assets/Auth.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { Messages } from "../messages/Messages";
import { FormData } from "../model/FormData";
import { toast } from "react-toastify";
import LoginRegisterNavbar from "./LoginRegisterNavbar";
import "../assets/LoginRegister.css"

import { registerUser } from "../service/RegisterService";
import { isValidName, isValidEmail, isValidMobile, isValidPassword, isValidAddress, isValidArea, isValidState, isValidCity, isValidPinCode, isValidForm } from "../validators/Validations";
import { City, State } from "country-state-city";


const Register = () => {

  const navigate = useNavigate();

  const [formErrors, setFormErrors] = useState({
    name: "",
    email: "",
    password: "",
    mobileNumber: "",
    addressLine1: "",
    addressLine2: "",
    area: "",
    city: "",
    state: "",
    pinCode: ""
  })
  const [formData, setFormData] = useState<FormData>({
    name: "",
    email: "",
    password: "",
    mobileNumber: "",
    addressLine1: "",
    addressLine2: "",
    area: "",
    city: "",
    state: "",
    pinCode: ""
  });

  const validateField = (name: string, value: string) => {
    let errors = { ...formErrors };
    switch (name) {
      case "name":
        errors.name = isValidName(value)
          ? ""
          : Messages.NAME_ERROR;
        break;
      case "email":
        errors.email = isValidEmail(value)
          ? ""
          : Messages.INVALID_EMAIL;
        break;
      case "mobileNumber":
        errors.mobileNumber = isValidMobile(value)
          ? ""
          : Messages.INVALID_MOBILE;
        break;
      case "password":
        errors.password = isValidPassword(value)
          ? ""
          : Messages.INVALID_PASSWORD;
        break;
      case "addressLine1":
        errors.addressLine1 = isValidAddress(value)
          ? ""
          : Messages.INVALID_ADDRESS;
        break;
      case "addressLine2":
        errors.addressLine2 = isValidAddress(value)
          ? ""
          : Messages.INVALID_ADDRESS;
        break;
      case "area":
        errors.area = isValidArea(value)
          ? ""
          : Messages.INVALID_AREA;
        break;
      case "city":
        errors.city = isValidCity(value)
          ? ""
          : Messages.INVALID_CITY;
        break;
      case "pinCode":
        errors.pinCode = isValidPinCode(value)
          ? ""
          : Messages.INVALID_PINCODE;
        break;
      case "state":
        errors.state = isValidState(value)
          ? ""
          : Messages.INVALID_STATE;
        break;
    }
    setFormErrors(errors);

  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;

    setFormData({ ...formData, [name]: value });
    validateField(name, value);
  };

  const isFormValid = () => {

    const noErrors = Object.values(formErrors).every(err => err === "");

    const allFilled = Object.values(formData).every(val => val !== "");

    return noErrors && allFilled;

  };
  const formValid = isValidForm(formData);
  const handleRegister = async (e: React.FormEvent) => {

    e.preventDefault();

    if (!isFormValid()) {
      return;

    }
    try {

      await registerUser(formData);

      toast.success("Registered Successfully.Please Login");
      setTimeout(() => {
        navigate("/login");
      }, 700)


    } catch (error :any) {
      toast.error(error.response.data.errorMessage);
    }
  };

  return (
    <>
      <LoginRegisterNavbar />
      <div className="auth-wrapper">
        <div className="auth-card register">
          <h3 className="auth-title">Create Your Account</h3>

          

          <form onSubmit={handleRegister}>
            <div className="mb-3">
              <label htmlFor="name" className="form-label">Full Name</label>
              <input
                type="text"
                className="form-control"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Enter full name"
              />
            </div>
            {formErrors.name && (<small className="text-danger">{formErrors.name}</small>)}

            <div className="mb-3">
              <label htmlFor="email" className="form-label">Email</label>
              <input
                type="email"
                className="form-control"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Enter email"
              />
            </div>
            {formErrors.email && (<small className="text-danger">{formErrors.email}</small>)}

            <div className="mb-3">
              <label htmlFor="number" className="form-label">Mobile Number</label>
              <input
                type="tel"
                className="form-control"
                name="mobileNumber"
                value={formData.mobileNumber}
                onChange={handleChange}
                placeholder="Enter mobile number"
              />
            </div>
            {formErrors.mobileNumber && (<small className="text-danger">{formErrors.mobileNumber}</small>)}


            <div className="mb-3">
              <label htmlFor="addressline1" className="form-label">Address Line1</label>
              <textarea
                rows={3}
                className="form-control"
                name="addressLine1"
                value={formData.addressLine1}
                onChange={handleChange}
                placeholder="Enter address"
              />
            </div>
            {formErrors.addressLine1 && (<small className="text-danger">{formErrors.addressLine1}</small>)}

            <div className="mb-3">
              <label htmlFor="addressLine2" className="form-label">Address Line2</label>
              <textarea
                rows={3}
                className="form-control"
                name="addressLine2"
                value={formData.addressLine2}
                onChange={handleChange}
                placeholder="Enter address"
              />
            </div>
            {formErrors.addressLine2 && (<small className="text-danger">{formErrors.addressLine2}</small>)}

            <div className="mb-3">
              <label htmlFor="area" className="form-label">Area</label>
              <input
                type="text"
                className="form-control"
                name="area"
                value={formData.area}
                onChange={handleChange}
                placeholder="Enter Area"
              />
            </div>
            {formErrors.area && (<small className="text-danger">{formErrors.area}</small>)}


            <div className="mb-3">
              <label htmlFor="state" className="form-label">State</label>
              <select name="state" className="form-control" value={formData.state} onChange={handleChange} id="">
                <option value="">Select State</option>
                {State.getStatesOfCountry("IN").map((state) => (
                  <option key={state.isoCode} value={state.isoCode}>{state.name}</option>
                ))}
              </select>
            </div>
            {formErrors.state && (<small className="text-danger">{formErrors.state}</small>)}

            <div className="mb-3">
              <label htmlFor="city" className="form-label">City</label>
              <select

                className="form-control"

                name="city"

                value={formData.city}

                onChange={handleChange}

                disabled={!formData.state}

              >

                <option value="">Select City</option>

                {City.getCitiesOfState("IN", formData.state).map((city) => (

                  <option key={city.name} value={city.name}>

                    {city.name}

                  </option>

                ))}

              </select>
            </div>
            {formErrors.city && (<small className="text-danger">{formErrors.city}</small>)}

            <div className="mb-3">
              <label htmlFor="pincode" className="form-label">Pincode</label>
              <input
                type="number"
                className="form-control"
                name="pinCode"
                value={formData.pinCode}
                onChange={handleChange}
                placeholder="Enter Pincode"
              />
            </div>
            {formErrors.pinCode && (<small className="text-danger">{formErrors.pinCode}</small>)}
            <div className="mb-3">
              <label htmlFor="password" className="form-label">Password</label>
              <input
                type="password"
                className="form-control"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Create password"
              />
            </div>
            {formErrors.password && (<small className="text-danger">{formErrors.password}</small>)}



            <button type="submit" className="btn btn-warning w-100 mt-2" disabled={!formValid} >
              Register
            </button>
          </form>

          <div className="auth-footer">
            Already have an account?{" "}
            <span className="auth-link" onClick={() => navigate("/login")} role="button" tabIndex={0}
              onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") {
                  navigate("/login")
                }
              }} >
              Login
            </span>
          </div>
        </div>
      </div>
    </>
  );
};

export default Register;
