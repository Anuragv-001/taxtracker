import axios from "axios";
import { Form90C } from "../model/Form90C";


const api = axios.create({
  baseURL: "http://localhost:8770", 
});


api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);


export const saveForm90C = (data: Form90C) => {
  return api.post("/tax-tracker/forms/90c", data);
};


export const submitForm90C = (data:number) => {
  return api.post("/tax-tracker/submissions",{
    formId:data
});
};


export const getSavedForm90C =async (): Promise<Form90C | null> => {
  const email=localStorage.getItem("userEmail");
  try{
      const response=await api.get(`/tax-tracker/forms/90c/${email}`);
      return response.data;
  }
  catch(error){
    console.error("Failed to fetch data",error)
    return null;
  }
};


export const uploadDocument = async (payload: {
 
  name: string;
  data: string ;
}) => {
  return api.post("/tax-tracker/uploads", payload);
};


