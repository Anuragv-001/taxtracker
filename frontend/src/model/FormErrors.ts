type FormErrors = {
    name?: string;
      mobileNumber ?:string,
      financialYear ? : string,
      transactions ? : {        
          transactionDate?: string;
          organizationName?: string;
          amount?:string;
          taxAmount?: string;
          type?: string;
        }[];
};

export default FormErrors;