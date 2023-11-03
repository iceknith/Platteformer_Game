package GameObjects;

import handlers.KeyHandler;

import java.util.ArrayList;

public class SubLevel {

    ArrayList<GameObject2D> objectList = new ArrayList<>();
    ArrayList<GameObject2D> permaDisplayedObjects = new ArrayList<>();
    String name;

    public boolean isDisplayed;
    public boolean isUpdated;

    //button menu handling
    ArrayList<ArrayList<Button>> buttons = new ArrayList<>();
    int focusedButtonIndexX;
    int focusedButtonIndexY;
    double lastDownTime = 0;
    double lastUpTime = 0;
    double lastLeftTime = 0;
    double lastRightTime = 0;

    public SubLevel(String subLevelName){
        name = subLevelName;
    }

    public SubLevel(String subLevelName, ArrayList<GameObject2D> goList) throws Exception {
        name = subLevelName;

        for (GameObject2D go: goList) {
            go.setSubLevelName(name);
            if (go.type.contains("Button_")){
                addButton(go.getButton());
            }
            if (go.isGUI) permaDisplayedObjects.add(go);
            objectList.add(go);
        }
        if (!buttons.isEmpty()){
            buttons.get(buttons.size() - 1).get(buttons.get(buttons.size() - 1).size() - 1).setKey_focused(true);
            focusedButtonIndexX = buttons.size() - 1;
            focusedButtonIndexY = buttons.get(buttons.size() - 1).size() - 1;
        }
    }

    public String getName(){return name;}

    public ArrayList<GameObject2D> getObjectList(){return objectList;}

    public void addObject(GameObject2D go) throws Exception {
        objectList.add(go);
        go.setSubLevelName(name);

        if (go.type.contains("Button_")) addButton(go.getButton());

        if (go.isGUI) permaDisplayedObjects.add(go);
    }

    void addButton(Button b){
        if (buttons.isEmpty()){
            buttons.add(new ArrayList<>());
            buttons.get(0).add(b);
            return;
        }

        // find X pos in list
        int x = 0;
        while (x < buttons.size() && b.getX() < buttons.get(x).get(0).getX()) {
            x++;
        }

        if (x == buttons.size()) buttons.add(new ArrayList<>());
        else {

            Button b2 = buttons.get(x).get(0);

            if (!(b.getX() >= b2.getX() && b.getX() <= b2.getX() + b2.getWidth())){
                buttons.add(x, new ArrayList<>());
            }
        }


        // find Y pos in list
        int y = 0;
        while (y < buttons.get(x).size() && b.getY() < buttons.get(x).get(y).getY()) {
            y++;
        }


        // adding the button to the list
        if (buttons.get(x).isEmpty()) buttons.get(x).add(b);
        else buttons.get(x).add(y, b);
    }

    void displayButtonList(){
        for (ArrayList<Button> button : buttons) {
            System.out.println();
            for (Button value : button) {
                System.out.print(value.buttonMessage + " | ");
            }
        }
    }

    public void update(){
        //button handling
        if (!buttons.isEmpty()){

            if(KeyHandler.isUpPressed && System.nanoTime() - lastUpTime >= 200000000){
                lastUpTime = System.nanoTime();

                buttons.get(focusedButtonIndexX).get(focusedButtonIndexY).setKey_focused(false);
                focusedButtonIndexY = (focusedButtonIndexY +1)%buttons.get(focusedButtonIndexX).size();
                buttons.get(focusedButtonIndexX).get(focusedButtonIndexY).setKey_focused(true);
            }

            if(KeyHandler.isDownPressed && System.nanoTime() - lastDownTime >= 200000000){
                lastDownTime = System.nanoTime();

                buttons.get(focusedButtonIndexX).get(focusedButtonIndexY).setKey_focused(false);
                focusedButtonIndexY = (focusedButtonIndexY + buttons.get(focusedButtonIndexX).size() - 1)%buttons.get(focusedButtonIndexX).size();
                buttons.get(focusedButtonIndexX).get(focusedButtonIndexY).setKey_focused(true);
            }

            if(KeyHandler.isLeftPressed && System.nanoTime() - lastLeftTime >= 200000000){
                lastLeftTime = System.nanoTime();

                buttons.get(focusedButtonIndexX).get(focusedButtonIndexY).setKey_focused(false);
                focusedButtonIndexX = (focusedButtonIndexX +1)%buttons.size();
                focusedButtonIndexY = Math.max(Math.min(buttons.get(focusedButtonIndexX).size() - 1, focusedButtonIndexY), 0); //clamping the Index Y value
                buttons.get(focusedButtonIndexX).get(focusedButtonIndexY).setKey_focused(true);
            }

            if(KeyHandler.isRightPressed && System.nanoTime() - lastRightTime >= 200000000){
                lastRightTime = System.nanoTime();

                buttons.get(focusedButtonIndexX).get(focusedButtonIndexY).setKey_focused(false);
                focusedButtonIndexX = (focusedButtonIndexX + buttons.size() - 1)%buttons.size();
                focusedButtonIndexY = Math.max(Math.min(buttons.get(focusedButtonIndexX).size() - 1, focusedButtonIndexY), 0); //clamping the Index Y value
                buttons.get(focusedButtonIndexX).get(focusedButtonIndexY).setKey_focused(true);
            }

        }
    }
}
