import React from "react";
import { Navigate } from "react-router-dom";
type Props={
    children:React.ReactElement;
}
const ProtectedRoutes=({children}:Props)=>{
    const token=localStorage.getItem("accessToken")
    if(!token)
    {
        return<Navigate to="/login" replace/>
    }
    return children;
}

export default ProtectedRoutes;