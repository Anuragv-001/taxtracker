import axios from "axios";

// models/Transaction.ts
export interface Transaction {
  txnId:number;
  date:string;
  amount: number;
  taxAmount: number;
  type: string;
  organizationName: string;
  // add only fields you show in UI
}

export interface TransactionPageResponse {
  pageNumber: number;
  pageSize: number;
  totalRecords: number;
  transactions: Transaction[];
}

export const getTransactions = (params: {
  pageNumber: number;
  pageSize: number;
  financialYear?: string;
  month?: number;
}) => {
  return axios.get<TransactionPageResponse>(
    "http://localhost:8770/tax-tracker/transactions",
    {
      params,
      headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`
      }
    }
  );
};

export const downloadAllTransactions = () => {
  return axios.get<Transaction[]>("http://localhost:8770/tax-tracker/transactions/download",
     { headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`
      }
    }
  );
};

export const extractApiError = (err: any): string => {
  if (!axios.isAxiosError(err) || !err.response) {
    return "Server is currently unavailable. Please try again later";
  }

  if (err.response.status === 401) {
    return "Session expired. Please login again";
  }

  return err.response.data?.errorMessage || "Something went wrong";
};



///Code to be added in tsx file 

