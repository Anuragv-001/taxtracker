import axios from "axios"



const URL="http://localhost:8770/tax-tracker/login"
export const loginUser=async(email:string,password:string)=>{
    const res=await axios.post(URL,{
        email,password
    })
    
    localStorage.setItem("accessToken",res.data.token);
    localStorage.setItem("userName",res.data.userName);
    localStorage.setItem("userEmail",res.data.email);
    return res.data;
}



