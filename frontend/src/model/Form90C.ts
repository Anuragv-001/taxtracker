export interface Form90CTransaction {    
  organizationName: string;
  amount: number|"";
  taxAmount: number|"";
  type: string ;
  txnDate:string;
}

export interface Form90C {  
  name: string;
  mobileNumber: string;
  financialYear: string;
  transactionHistory: Form90CTransaction[];
}

