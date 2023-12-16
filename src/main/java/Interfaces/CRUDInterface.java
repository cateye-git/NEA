package Interfaces;

import javafx.event.ActionEvent;

public interface CRUDInterface {
    //C
    public void addNew(ActionEvent event);
    //R
    public String[] getEntities();
    //U
    public void editSelected(ActionEvent event);
    //D
    public void deleteSelected(ActionEvent event);

    //extra
    public void copySelected(ActionEvent event);
}
