import axios from "axios";

const API_URL="http://localhost:8770/tax-tracker/register";



export const registerUser=async(formData:any)=>{
    const res=await axios.post(API_URL,formData);
    return res.data;
}

