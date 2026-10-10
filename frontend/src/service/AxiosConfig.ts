import axios from "axios";

import { getToken,clearToken } from "./Token";

const api = axios.create({

  baseURL: "http://localhost:5000",

});

api.interceptors.request.use(

  (config) => {

    const token = getToken();

    if (token) {

      config.headers.Authorization = `Bearer ${token}`;

    }

    return config;

  },

  (error) => Promise.reject(error)

);



api.interceptors.response.use(

  (res) => res,

  (error) => {

    if (error.response?.status === 401) {

      clearToken();

      globalThis.location.href = "/login";

    }

    return Promise.reject(error);

  }

);

export default api;


