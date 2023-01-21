package GameObjects;

import java.util.ArrayList;

public class SubLevel {

    public SubLevel(String subLevelName){
        name = subLevelName;
    }

    public SubLevel(String subLevelName, ArrayList<GameObject2D> goList){
        name = subLevelName;
        objectList = goList;
        for (GameObject2D go: objectList) {
            go.setSubLevelName(name);
        }
    }

    ArrayList<GameObject2D> objectList = new ArrayList<>();
    String name;
    
    public boolean isDisplayed;
    public boolean isUpdated;

    public String getName(){return name;}

    public ArrayList<GameObject2D> getObjectList(){return objectList;}

    public void addObject(GameObject2D object){objectList.add(object);}

}
