import { useState } from "react";
import { submitForm90C, uploadDocument } from "../service/Form90CService";
import { useNavigate, useLocation } from "react-router-dom";
import { toast } from "react-toastify";
import Navbar from "./Navbar";
import "../assets/Auth.css";


export default function Form90CPart2() {
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const [files, setFiles] = useState<File[]>([]);
  const [previewFiles, setPreviewFiles] = useState<string[]>([]);
  const [isUploaded, setIsUploaded] = useState(false);

 

  const validateFiles = (files: File[]): boolean => {
    if (files.length === 0) {
      toast.error("Please upload atleast one document");
      return false;
    }

    for (const file of files) {
      if (!["application/pdf", "image/jpeg", "image/png"].includes(file.type)) {
        toast.error("Invalid file Format");
        return false;
      }

      if (file.size > 2 * 1024 * 1024) {
        toast.error("File size exceeds 2MB");
        return false;
      }
    }
    return true;
  };

  const fileToBase64=(file:File):Promise<string> =>{
      return new Promise((resolve,reject)=>{
        const reader=new FileReader();
        reader.onload=()=>{
          const result=reader.result as string;
          resolve(result.split(",")[1]);
        };
        reader.onerror=reject;
        reader.readAsDataURL(file);
      })
  }
  const handleUpload = async () => {
    if (!validateFiles(files)) return;

    try {
      for (const file of files) {
        const base64Data = await fileToBase64(file);

        const payload={
          name:file.name,
          data:base64Data
        }
        await uploadDocument(payload);
      }
      toast.success("Document uploaded");
      setIsUploaded(true);
      setFiles([]);
    } catch (error: any) {
      toast.error("Document upload failed: " + error.message);
    }
  };

  const handleBack = () => {
    navigate("/form-90c");
  };

  const handleSubmit = async () => {
    
    const formId=localStorage.getItem("formId");
    if(!formId){
      toast.error("form Id is missing");
      return;
    }
    await submitForm90C(Number(formId));

    

    toast.info("Form submitted Successfully");
    setTimeout(() => navigate("/login-success"), 700);
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files) return;

    const selectedFiles = Array.from(e.target.files);
    setFiles(prev => [...prev, ...selectedFiles]);

    const previews = selectedFiles.map(file =>
      URL.createObjectURL(file)
    );
    setPreviewFiles(prev => [...prev, ...previews]);

    e.target.value = "";
  };

  const removeFile = (index: number) => {
    setFiles(prev => prev.filter((_, i) => i !== index));
    setPreviewFiles(prev => prev.filter((_, i) => i !== index));
  };

  return (
    <>
      {/* ✅ NAVBAR */}
      <Navbar onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} />

      <div className={`app-layout ${sidebarOpen ? "sidebar-open" : "sidebar-closed"}`}>

        {/* ✅ SIDEBAR */}
        <aside className={`sidebar ${sidebarOpen ? "open" : "closed"}`}>
          <button

  type="button"

  className={`sidebar-item ${location.pathname === "/login-success" ? "active" : ""}`}

  onClick={() => navigate("/login-success")}

>

  🏠 Dashboard

</button>


          <button

  type="button"

  className={`sidebar-item ${location.pathname === "/login-success" ? "active" : ""}`}

  onClick={() => navigate("/login-success")}

>
            📊 Transactions
          </button>

          <div
            className={`sidebar-item ${location.pathname.startsWith("/form-90c") ? "active" : ""}`}
          >
            📝 Form 90C
          </div>
        </aside>

        
        <main className="dashboard-page">
          <div className="container mt-4">

            <div className="head text-center">
              <h3><b>FORM 90C - Upload Documents</b></h3>
            </div>

            <input
              type="file"
              className="form-control mb-3"
              accept=".pdf,.jpg"
              multiple
              onChange={handleFileChange}
              disabled={isUploaded}
            />

            <div className="mt-3">
              {previewFiles.map((preview, index) => (
                <div key={preview} className="mb-2 d-flex align-items-center gap-2">
                  {files[index]?.type.startsWith("image/") ? (
                    <img
                      src={preview}
                      alt="preview"
                      width={80}
                      height={80}
                      style={{ objectFit: "cover" }}
                    />
                  ) : (
                    <span>{files[index]?.name}</span>
                  )}

                  {!isUploaded && (
                    <button
                      type="button"
                      className="btn btn-sm btn-danger"
                      onClick={() => removeFile(index)}
                    >
                      Remove
                    </button>
                  )}
                </div>
              ))}
            </div>

            <button className="btn btn-secondary me-2" onClick={handleUpload}>
              Upload Document
            </button>

            <button className="btn btn-secondary me-2" onClick={handleBack}>
              Go Back
            </button>

            <button
              className="btn btn-primary"
              onClick={handleSubmit}
              disabled={!isUploaded}
            >
              Submit Form
            </button>

           

          </div>
        </main>
      </div>
    </>
  );
}
