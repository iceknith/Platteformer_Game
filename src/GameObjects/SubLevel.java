package GameObjects;

import handlers.KeyHandler;

import java.util.ArrayList;

public class SubLevel {

    public SubLevel(String subLevelName){
        name = subLevelName;
    }

    public SubLevel(String subLevelName, ArrayList<GameObject2D> goList) throws Exception {
        name = subLevelName;
        objectList = goList;
        for (GameObject2D go: objectList) {
            go.setSubLevelName(name);
            if (go.type.contains("Button_")){
                buttons.add(go.getButton());
            }
        }
        if (!buttons.isEmpty()){
            buttons.get(0).setKey_focused(true);
            focusedButtonIndex = 0;
        }
    }

    ArrayList<GameObject2D> objectList = new ArrayList<>();
    String name;
    
    public boolean isDisplayed;
    public boolean isUpdated;

    //button menu handling
    ArrayList<Button> buttons = new ArrayList<>();
    int focusedButtonIndex;
    double lastDownTime = 0;
    double lastUpTime = 0;

    public String getName(){return name;}

    public ArrayList<GameObject2D> getObjectList(){return objectList;}

    public void addObject(GameObject2D object){objectList.add(object);}

    public void update(){
        //button handling
        if (!buttons.isEmpty()){

            if(KeyHandler.isDownPressed && System.nanoTime() - lastDownTime >= 200000000){
                lastDownTime = System.nanoTime();

                buttons.get(focusedButtonIndex).setKey_focused(false);
                focusedButtonIndex = (focusedButtonIndex+1)%buttons.size();
                buttons.get(focusedButtonIndex).setKey_focused(true);
            }

            if(KeyHandler.isUpPressed && System.nanoTime() - lastUpTime >= 200000000){
                lastUpTime = System.nanoTime();

                buttons.get(focusedButtonIndex).setKey_focused(false);
                focusedButtonIndex = (focusedButtonIndex+buttons.size()-1)%buttons.size();
                buttons.get(focusedButtonIndex).setKey_focused(true);
            }

        }
    }
}
