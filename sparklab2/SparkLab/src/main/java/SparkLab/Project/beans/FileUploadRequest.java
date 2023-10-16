package SparkLab.Project.beans;


import java.util.List;

public class FileUploadRequest {
    private String fileName;
    private String fileType;
    private List<Integer> fileData;

    // Costruttore, getter e setter
    public FileUploadRequest(){}
    public String getFileType() {
        return fileType;
    }

    public List<Integer> getFileData() {
        return fileData;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileData(List<Integer> fileData) {
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }
}