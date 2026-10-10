
import { FormData } from "../model/FormData";

export const isValidName=(name:string):boolean=>{
    const regex=/^[A-Z][a-z]+ [A-Z][a-z]+$/;
    return regex.test(name.trim());
}
export const isValidEmail=(email:string):boolean=>{
        const regex=/^[^@\s]+@[^@\s]+\.[A-Za-z]{2,}$/;
        return regex.test(email);
    };

    export const isValidPassword=(password:string):boolean=>{
        return password.length>=7;
    } 

    export const isValidMobile=(mobile:string):boolean=>{
        return /^[6-9]\d{9}$/.test(mobile);
    }

    export const isValidPinCode=(pin_code:string):boolean=>{
        return /^\d{6}$/.test(pin_code);
    }

    export const isValidAddress=(address:string):boolean=>{
        
        return address.trim().length>0;

    }

    export const isValidCity=(city:string):boolean=>{
        return /^[A-Za-z\s]{2,50}$/.test(city.trim());
    }

    export const isValidState=(state:string):boolean=>{
        return /^[A-Za-z\s]{2,50}$/.test(state.trim());
    }
   
    export const isValidArea=(area:string):boolean=>{
        return /^[A-Za-z\s]{2,50}$/.test(area.trim());
    }

   
    export const isValidForm=(formData:FormData):boolean=>{
        return(isValidName(formData.name) && isValidEmail(formData.email) && isValidPassword(formData.password) && isValidMobile(formData.mobileNumber) && isValidPinCode(formData.pinCode) && isValidAddress(formData.addressLine1)
        && isValidArea(formData.area) && isValidCity(formData.city) && isValidState(formData.state));
    }