import axios from "axios";

export const logout = () => {

  const token=localStorage.getItem("accessToken");

  if(!token){
    return Promise.resolve();
  }
  return axios.get("http://localhost:8770/tax-tracker/logout",
     { headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );
};


