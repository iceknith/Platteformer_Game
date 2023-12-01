package GameObjects;

import handlers.KeyHandler;
import handlers.MouseHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class LevelMaker extends GameObject2D{
    //A Game object, that performs more as a handler.
    //It allows for the creation of levels inside the game,
    //WARNING: currently it can only be used in the main class

    public static boolean cameraCanMove = true;
    boolean canPlaceObj = true;
    public static boolean objIsPlaced = true;

    long lastButtonUpdateTime;

    ArrayList<GameObject2D> objects = new ArrayList<>();
    int id_counter = 6;

    boolean isLevelLaunched = false;
    DropDownMenu rightClickMenu;
    TextInputMenu txtInputMenu;

    ArrayList<Button> buttons = new ArrayList<>();
    HashMap<String,ArrayList<String>> platformTextures = new HashMap<>();
    HashMap<String,ArrayList<String>> platformTextureNames = new HashMap<>();
    HashMap<String, ArrayList<Image>> platformImages = new HashMap<>();
    HashMap<String, ArrayList<Character>> platformUtilTypes = new HashMap<>();
    HashMap<String, ArrayList<Integer>> platformFrameCount = new HashMap<>();

    HashMap<String,ArrayList<String>> imageTextures = new HashMap<>();
    HashMap<String,ArrayList<String>> imageTextureNames = new HashMap<>();
    HashMap<String, ArrayList<Image>> imageImages = new HashMap<>();
    HashMap<String, ArrayList<Integer>> imageFrameCount = new HashMap<>();

    ArrayList<String> backgroundTextureNames = new ArrayList<>();
    ArrayList<int[]> backgroundDimensions = new ArrayList<>();
    Background background = null;

    int maxButtonY;

    int defaultObjWidth = 100;
    int defaultObjHeight = 100;

    String nextObjType = "";
    String nextObjTexture = "";
    char nextObjUtilType = 'b';
    int nextObjFrameCount = 0;
    

    boolean isInGridMode = false;
    int gridCellWidth = 64;
    int gridCellHeight = 64;


    LevelMaker() throws IOException, FontFormatException {
        super(0, 0, 0, 0, "");

        type = "LevelMaker_";
        name = type + "0";

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/placeholder.png")), hitbox);

        int buttonHeight = 75;
        rightClickMenu = new DropDownMenu(0,0, 150, 3*buttonHeight, "# " + id_counter, "",
                new ArrayList<>(), new ArrayList<>());
        id_counter ++;
        txtInputMenu = new TextInputMenu(GamePanel.camera.screenWidth /2, GamePanel.camera.screenHeight /2,
                "#" + id_counter, "", true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        id_counter ++;

        initButtons();
    }

    @Override
    public void update() throws Exception {

        rightClickMenu.update();
        txtInputMenu.update();

        //if level is already launched
        if (isLevelLaunched){
            if (KeyHandler.isLaunchKeyPressed){

                KeyHandler.isLaunchKeyPressed = false;
                isLevelLaunched = false;
                GamePanel.camera.level.updateLevelMaker = true;

                //unload all loaded GO in level, and replace them with the saved ones
                Level lvl = GamePanel.camera.level;
                lvl.permanentUpdatable = new ArrayList<>();
                lvl.getSubLvl("main").objectList = new ArrayList<>();
                lvl.getSubLvl("main").permaDisplayedObjects = new ArrayList<>();
                lvl.getSubLvl("main").buttons = new ArrayList<>();
                GamePanel.camera.initialiseGrid(new ArrayList<>());

                lvl.addToMainSubLevel(this);
                for (GameObject2D go : objects){
                    lvl.addToMainSubLevel(go);

                    if (Objects.equals(go.getType(), "Player")){
                        player = go.getThisPlayer();

                        GamePanel.camera.screenX = player.getX() - GamePanel.camera.screenWidth /2;
                        GamePanel.camera.screenY = player.getY() - GamePanel.camera.screenHeight /2;
                    }
                }
            }
            else return;
        }

        if (System.nanoTime() - lastButtonUpdateTime > 50000000){ //have some sort of tick System, that updates the buttons every 0.5s since it is very laggy
            lastButtonUpdateTime = System.nanoTime();
            buttonLogic();
        }

        //close opened tabs logic
        if (MouseHandler.isRightClickPressed || MouseHandler.isLeftClickPressed){
            if (rightClickMenu.isOpen &&
                    !rightClickMenu.pointIsIn(MouseHandler.getX(), MouseHandler.getY())){

                MouseHandler.resetClicks();
                rightClickMenu.activate();
                GamePanel.camera.noUpdate = false;
            }
            else if(txtInputMenu.isOpen &&
                    (!txtInputMenu.pointIsIn(MouseHandler.getX(), MouseHandler.getY()) &&
                     !txtInputMenu.pointIsOnButton(MouseHandler.getX(), MouseHandler.getY())) ){
                MouseHandler.resetClicks();
                txtInputMenu.isOpen = false;
                GamePanel.camera.noUpdate = false;
            }
        }

        //place objects
        if (MouseHandler.isLeftClickPressed && canPlaceObj && objIsPlaced){

            int mouseX = MouseHandler.getX() + GamePanel.camera.screenX;
            int mouseY = MouseHandler.getY()  + GamePanel.camera.screenY;
            if (isInGridMode){
                if (mouseX >= 0) mouseX -= mouseX%gridCellWidth;
                else mouseX -= gridCellWidth + mouseX%gridCellWidth;

                if (mouseY > 0) mouseY -= mouseY%gridCellHeight;
                else mouseY -= gridCellHeight + mouseY%gridCellHeight;
            }

            //Check if the GO to be placed doesn't overlap with an existing GO
            Entity potentialGOSpace = new Entity(
                    mouseX + 1, mouseY + 1,
                    defaultObjWidth - 2, defaultObjHeight - 2,
                    "None");
            potentialGOSpace.type = "Placeholder";
            potentialGOSpace.name = "Placeholder";

            ArrayList<int[]> mouseGridPos = GamePanel.camera.findRectPosInGrid(potentialGOSpace);

            for (int[] pos : mouseGridPos){

                for (GameObject2D go : GamePanel.camera.grid.get(pos[0]).get(pos[1])){
                    if (potentialGOSpace.intersects(go)){
                        return;
                    }
                }
            }

            //if not: place go
            placeGo(mouseX, mouseY);
        }

        //edit placed objects
        if (MouseHandler.isRightClickPressed && !rightClickMenu.isOpen){

            int mouseX = MouseHandler.getX();
            int mouseY = MouseHandler.getY();

            ArrayList<int[]> mouseGridPos = GamePanel.camera.findPointPosInGrid(mouseX  + GamePanel.camera.screenX, mouseY + GamePanel.camera.screenY);

            for (int[] pos : mouseGridPos){

                for (GameObject2D go : GamePanel.camera.grid.get(pos[0]).get(pos[1])){

                    if (go.pointIsIn(mouseX, mouseY)){
                        editGoLogic(go, mouseX, mouseY);
                        return;
                    }
                }
            }

            if (player.pointIsIn(mouseX, mouseY)){
                editGoLogic(player, mouseX, mouseY);
            }
        }

        //Launch Level
        if (KeyHandler.isLaunchKeyPressed && !hasNoPlayer()){

            //move the camera to the player
            GamePanel.camera.setScreenX(GameObject2D.player.getX() - GamePanel.camera.getScreenWidth()/2);
            GamePanel.camera.setScreenY(GameObject2D.player.getY() - GamePanel.camera.getScreenHeight()/2);
            GamePanel.camera.updateGrid();

            //save every object in its actual state
            ArrayList<GameObject2D> saveObj = new ArrayList<>();
            for (GameObject2D go : objects){
                saveObj.add(go.copy());
            }
            objects = saveObj;

            KeyHandler.isLaunchKeyPressed = false;
            GamePanel.camera.level.updateLevelMaker = false;
            isLevelLaunched = true;
        }

    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {

        if (isLevelLaunched) return;

        //draw mouse coos
        g2D.setColor(Color.white);
        g2D.setFont(new Font("Eight Bit Dragon", Font.PLAIN, 20));
        g2D.drawString("X : " + (GamePanel.camera.screenX + MouseHandler.getX()),15,25);
        g2D.drawString("Y : " +  (GamePanel.camera.screenY + MouseHandler.getY()),15,50);


        if (isInGridMode){
            g2D.setColor(Color.darkGray);

            for (int x = - gridCellWidth - GamePanel.camera.getScreenX()%gridCellWidth; x <= GamePanel.camera.getScreenWidth(); x += gridCellWidth) {

                    g2D.drawLine(x, 0, x, GamePanel.camera.screenHeight);
                }
            for (int y = - gridCellHeight - GamePanel.camera.getScreenY()%gridCellHeight; y <= GamePanel.camera.getScreenHeight(); y += gridCellHeight) {

                g2D.drawLine(0 , y, GamePanel.camera.screenWidth, y);
            }
        }

        for (Button b : buttons){
            b.draw(g2D, IO);
        }

        rightClickMenu.draw(g2D, IO);
        txtInputMenu.draw(g2D, IO);
    }

    public void placeGo(int x, int y) throws Exception {
        id_counter += 1;

        objIsPlaced = false;
        //player
        if (hasNoPlayer()){
            Player p = new Player(x, y,
                    "#0", "");
            GamePanel.camera.level.addToMainSubLevel(p);
            objects.add(p);
            player = p;
        }
        //platform
        else if (nextObjType.equals("Platform")) {
            Platform p = new Platform(defaultObjWidth, defaultObjHeight, x, y,
                    nextObjUtilType, nextObjTexture, nextObjFrameCount, "#"+id_counter, "");

            GamePanel.camera.level.addToMainSubLevel(p);
            objects.add(p);
        }
        //image
        else if (nextObjType.equals("Image")){
            ImageObject i = new ImageObject(x, y, nextObjTexture, nextObjFrameCount, "#"+ id_counter, "");

            if (isInGridMode){
                i.setX(x + gridCellWidth/2 -  i.getWidth()/2);
                i.setY(y + gridCellHeight - i.getHeight());
            }

            GamePanel.camera.level.addToMainSubLevel(i);
            objects.add(i);
        }
        //checkpoint
        else if (nextObjType.equals("Checkpoint")) {
            if (isInGridMode) {
                x += gridCellWidth/2 - 28/2;
                y -= 85%gridCellHeight;
            }

            CheckPoint c = new CheckPoint(x, y, "#"+id_counter, "");
            GamePanel.camera.level.addToMainSubLevel(c);
            objects.add(c);
        }
        else if (nextObjType.equals("Moving Platform")){
            MovingPlatform m = new MovingPlatform(x, y, x+20, y+20, defaultObjWidth, defaultObjHeight,
                    nextObjTexture, nextObjFrameCount, "#" + id_counter, "");

            GamePanel.camera.level.addToMainSubLevel(m);
            objects.add(m);
        }
        else{
            objIsPlaced = true;
        }
    }

    public void editGoLogic(GameObject2D go, int pointX, int pointY){

        Function<Void, Void> delete =
                unused -> {
            objects.remove(go);
            GamePanel.camera.level.getSubLvl("main").objectList.remove(go);
            GamePanel.camera.deleteGOInGrid(go);
            GamePanel.camera.updateGrid();

            rightClickMenu.activate();
            GamePanel.camera.noUpdate = false;

            MouseHandler.resetClicks();

            return unused;
        };

        Function<Void, Void> resize =
                unused -> {
            txtInputMenu.setAsInt(true);

            txtInputMenu.setCategoryNames(Arrays.asList("Width: ", "Height: "));
            txtInputMenu.setDefaultValues(Arrays.asList(String.valueOf(go.getWidth()), String.valueOf(go.getHeight())));
            txtInputMenu.setCategorySetValues(Arrays.asList(
                    s -> {
                        int i = Integer.parseInt(s);
                        GamePanel.camera.deleteGOInGrid(go);
                        go.setWidth(i);
                        go.sprite.setWidth(i);
                        GamePanel.camera.addGOInGrid(go);
                        if (!isInGridMode) defaultObjWidth = i;
                        return null;
                        },
                    s -> {
                        int i = Integer.parseInt(s);
                        GamePanel.camera.deleteGOInGrid(go);
                        go.setHeight(i);
                        go.sprite.setHeight(i);
                        GamePanel.camera.addGOInGrid(go);
                        if (!isInGridMode) defaultObjHeight = i;
                        canPlaceObj = true;
                        return null;
                    }));

            txtInputMenu.isOpen = true;
            rightClickMenu.activate();

            canPlaceObj = false;
            MouseHandler.resetClicks();

            return unused;
        };

        Function<Void, Void> move =
                unused -> {
            txtInputMenu.setAsInt(true);

            txtInputMenu.setCategoryNames(Arrays.asList("Position X: ", "Position Y: "));
            txtInputMenu.setDefaultValues(Arrays.asList(String.valueOf(go.getX()), String.valueOf(go.getY())));
            txtInputMenu.setCategorySetValues(Arrays.asList(
                    s -> {
                        int i = Integer.parseInt(s);
                        GamePanel.camera.deleteGOInGrid(go);
                        go.setX(i);
                        if (go.name.contains("Player")) player.spawnPointPos[0] = i;
                        GamePanel.camera.addGOInGrid(go);
                        return null;},

                    s -> {int i = Integer.parseInt(s);
                        GamePanel.camera.deleteGOInGrid(go);
                        go.setY(i);
                        if (go.name.contains("Player")) player.spawnPointPos[1] = i;
                        GamePanel.camera.addGOInGrid(go);
                        canPlaceObj = true;
                        return null;
                    }));

            txtInputMenu.isOpen = true;
            rightClickMenu.activate();

            canPlaceObj = false;
            MouseHandler.resetClicks();

            return unused;
        };

        rightClickMenu.setX(pointX);
        rightClickMenu.setY(pointY);
        rightClickMenu.setDisplayedWidth(150);
        rightClickMenu.setHasImages(false);

        if (go.type.contains("Player")){
            rightClickMenu.setButtonText(List.of("Move"));
            rightClickMenu.setButtonExec(List.of(move));

            rightClickMenu.setHeight(rightClickMenu.buttonHeight);
        }

        else if (go.type.contains("Checkpoint")){
            rightClickMenu.setButtonText(Arrays.asList("Delete", "Move"));
            rightClickMenu.setButtonExec(Arrays.asList(delete, move));

            rightClickMenu.setHeight((rightClickMenu.buttonHeight+10)*2 - 10);
        }
        else{
            rightClickMenu.setButtonText(Arrays.asList("Delete", "Resize", "Move"));
            rightClickMenu.setButtonExec(Arrays.asList(delete, resize, move));

            rightClickMenu.setHeight((rightClickMenu.buttonHeight+10)*3 - 10);
        }

        rightClickMenu.activate();

        GamePanel.camera.update();
        GamePanel.camera.noUpdate = true;
    }

    public void initButtons() throws IOException, FontFormatException {

        platformTextures = new HashMap<>();
        platformImages = new HashMap<>();
        platformTextureNames = new HashMap<>();
        platformUtilTypes = new HashMap<>();
        String lastButtonMsg = "";

        int x = 250;
        int y = GamePanel.camera.screenHeight /8 - 50;

        //read platform button info
        try {
            BufferedReader reader = new BufferedReader(new FileReader("assets/textureRegistery/Platform.textureReg"));
            String line = reader.readLine();

            while (line != null) {

                if (line.startsWith("-")){
                    lastButtonMsg = line.substring(1);

                    Button b = new Button(100, 75, x, y, "base", lastButtonMsg, "#" + id_counter, "");
                    b.buttonMessageColor = Color.yellow;

                    buttons.add(b);
                    id_counter ++;
                    x += 100;
                    if (x >= GamePanel.camera.screenWidth - 250){
                        x = 250;
                        y += 75;
                    }

                    platformTextures.put(lastButtonMsg, new ArrayList<>());
                    platformTextureNames.put(lastButtonMsg, new ArrayList<>());
                    platformImages.put(lastButtonMsg, new ArrayList<>());
                    platformUtilTypes.put(lastButtonMsg, new ArrayList<>());
                    platformFrameCount.put(lastButtonMsg, new ArrayList<>());
                }
                else{

                    char utilType;
                    switch (line.split(";")[1]){
                        case "killer" -> utilType = 'k';
                        case "win" -> utilType = 'w';
                        default -> utilType = 'b';
                    }

                    int frameCnt = Integer.parseInt(line.split(";")[2]);

                    line = line.split(";")[0];

                    String[] textureName = line.split("/");
                    platformTextures.get(lastButtonMsg).add(line);
                    platformTextureNames.get(lastButtonMsg).add(textureName[textureName.length - 1]);
                    platformImages.get(lastButtonMsg).add(ImageIO.read(new File("assets/Platform/" + line + "/0.png")));
                    platformUtilTypes.get(lastButtonMsg).add(utilType);
                    platformFrameCount.get(lastButtonMsg).add(frameCnt);

                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //read image button info
        try {
            BufferedReader reader = new BufferedReader(new FileReader("assets/textureRegistery/Image.textureReg"));
            String line = reader.readLine();

            while (line != null) {

                if (line.startsWith("-")){
                    lastButtonMsg = line.substring(1);

                    Button b = new Button(100, 75, x, y, "base", lastButtonMsg, "#" + id_counter, "");
                    b.buttonMessageColor = Color.cyan;

                    buttons.add(b);
                    id_counter ++;
                    x += 100;
                    if (x >= GamePanel.camera.screenWidth - 250){
                        x = 250;
                        y += 75;
                    }

                    imageTextures.put(lastButtonMsg, new ArrayList<>());
                    imageTextureNames.put(lastButtonMsg, new ArrayList<>());
                    imageImages.put(lastButtonMsg, new ArrayList<>());
                    imageFrameCount.put(lastButtonMsg, new ArrayList<>());
                }
                else{

                    int frameCnt = Integer.parseInt(line.split(";")[1]);

                    line = line.split(";")[0];

                    String[] textureName = line.split("/");
                    imageTextures.get(lastButtonMsg).add(line);
                    imageTextureNames.get(lastButtonMsg).add(textureName[textureName.length - 1]);
                    imageImages.get(lastButtonMsg).add(ImageIO.read(new File("assets/ImageObject/" + line + "/0.png")));
                    imageFrameCount.get(lastButtonMsg).add(frameCnt);

                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //read background button info
        try {
            BufferedReader reader = new BufferedReader(new FileReader("assets/textureRegistery/Background.textureReg"));
            String line = reader.readLine();

            while (line != null) {

                String[] lineSplit = line.split(";");
                backgroundTextureNames.add(lineSplit[0]);
                backgroundDimensions.add(new int[]{Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2])});
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //define other buttons
        for (String msg : Arrays.asList("Moving Platform","Checkpoint", "Background", "To Player", "Grid", "Load", "Save")){

            Button b = new Button(100, 75, x, y, "base", msg, "#" + id_counter, "");
            switch (msg){
                case "Checkpoint", "Background" -> b.buttonMessageColor = Color.orange;
                default -> b.buttonMessageColor = Color.pink;
            }
            buttons.add(b);
            id_counter ++;
            x += 100;
            if (x >= GamePanel.camera.screenWidth - 250){
                x = 250;
                y += 75;
            }
        }

        maxButtonY = y + 75;
    }

    void buttonLogic() throws IOException {

        cameraCanMove = true;
        canPlaceObj = !txtInputMenu.isOpen && !rightClickMenu.isOpen;
        if (MouseHandler.getY() > maxButtonY || txtInputMenu.isOpen || rightClickMenu.isOpen) return;

        for (Button b : buttons){
            if (mouseOverButton(b)){

                cameraCanMove = false;
                canPlaceObj = false;
                if (!b.isFocused()) b.focusHandler();

                if (MouseHandler.isLeftClickPressed){

                    MouseHandler.resetClicks();


                    switch (b.buttonMessage) {

                        case "Checkpoint" -> nextObjType = "Checkpoint";

                        case "Moving Platform" -> {
                            nextObjType = "Moving Platform";
                            nextObjTexture = "base/rainbow";
                            nextObjFrameCount = 5;
                        }

                        case "Background" -> {
                            rightClickMenu.setX(MouseHandler.getX());
                            rightClickMenu.setY(MouseHandler.getY());
                            rightClickMenu.setButtonText(backgroundTextureNames);
                            rightClickMenu.setHasImages(false);

                            ArrayList<Function<Void, Void>> buttonExec = new ArrayList<>();

                            //modify background button
                            if (background != null){
                                rightClickMenu.buttonText.add(0, "Settings");

                                buttonExec.add(unused -> {
                                    txtInputMenu.setAsInt(true);

                                    txtInputMenu.setCategoryNames(Arrays.asList(
                                            "x :", "y :", "Zoom :", "Scrolling Slowness :", "repeats infinitely x:", "repeats infinitely y:"
                                    ));
                                    txtInputMenu.setDefaultValues(Arrays.asList(
                                            String.valueOf(background.getX()), String.valueOf(background.getY()),
                                            String.valueOf(background.getZoom()), String.valueOf(background.getScrollingSlowness()),
                                            String.valueOf(background.getDoRepeatXInt()), String.valueOf(background.getDoRepeatYInt())
                                    ));
                                    txtInputMenu.setCategorySetValues(Arrays.asList(
                                            s -> {int i = Integer.parseInt(s); background.setX(i); canPlaceObj = true; return null;},
                                            s -> {int i = Integer.parseInt(s); background.setY(i);canPlaceObj = true;return null;},
                                            s -> {float i = Float.parseFloat(s); background.setZoom(Math.abs(i));canPlaceObj = true;return null;},
                                            s -> {float i = Float.parseFloat(s); background.setScrollingSlowness(Math.abs(i));canPlaceObj = true;return null;},
                                            s -> {int i = Integer.parseInt(s); background.setDoRepeatX(i > 0);canPlaceObj = true;return null;},
                                            s -> {int i = Integer.parseInt(s); background.setDoRepeatY(i > 0);canPlaceObj = true;return null;}
                                    ));

                                    txtInputMenu.isOpen = true;
                                    rightClickMenu.activate();

                                    canPlaceObj = false;
                                    MouseHandler.resetClicks();

                                    return unused;
                                });
                            }

                            //add background selection buttons
                            for (int i = 0; i < backgroundTextureNames.size(); i++){
                                int[] dim = backgroundDimensions.get(i);
                                int finalI = i;

                                buttonExec.add(unused -> {
                                    id_counter += 1;

                                    try {
                                        Background bg = new Background(dim[0], dim[1], backgroundTextureNames.get(finalI), "#" + id_counter, "");

                                        if (background != null){
                                            GamePanel.camera.level.getSubLvl("main").objectList.remove(background);
                                            objects.remove(background);
                                        }

                                        GamePanel.camera.level.addToMainSubLevel(bg);
                                        objects.add(bg);
                                        background = bg;

                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }

                                    rightClickMenu.activate();
                                    MouseHandler.resetClicks();
                                    GamePanel.camera.noUpdate = false;
                                    return unused;
                                });
                            }

                            rightClickMenu.setButtonExec(buttonExec);
                            rightClickMenu.setDisplayedWidth(500);
                            rightClickMenu.setHeight((rightClickMenu.buttonHeight + 10) * buttonExec.size() - 10);
                            if (!rightClickMenu.isOpen) {
                                rightClickMenu.activate();
                            }
                            GamePanel.camera.update();
                            GamePanel.camera.noUpdate = true;
                        }

                        case "To Player" -> {
                            if (GameObject2D.player != null){
                                GamePanel.camera.setScreenX(GameObject2D.player.getX() - GamePanel.camera.getScreenWidth()/2);
                                GamePanel.camera.setScreenY(GameObject2D.player.getY() - GamePanel.camera.getScreenHeight()/2);
                                GamePanel.camera.updateGrid();
                            }
                        }

                        case "Grid" -> {
                            if (!isInGridMode) {

                                txtInputMenu.setAsInt(true);

                                txtInputMenu.setCategoryNames(List.of("Grid Cell Width", "Grid Cell Height"));
                                txtInputMenu.setDefaultValues(List.of(Integer.toString(gridCellWidth), Integer.toString(gridCellHeight)));
                                txtInputMenu.setCategorySetValues(List.of(
                                        s -> {
                                            int i = Integer.parseInt(s);
                                            gridCellWidth = i;
                                            defaultObjWidth = i;
                                            return null;
                                        },
                                        s -> {
                                            int i = Integer.parseInt(s);
                                            gridCellHeight = i;
                                            defaultObjHeight = i;
                                            isInGridMode = true;
                                            canPlaceObj = true;
                                            return null;
                                        }
                                ));

                                txtInputMenu.isOpen = true;
                                canPlaceObj = false;

                                GamePanel.camera.update();
                                GamePanel.camera.noUpdate = true;
                            } else {
                                isInGridMode = false;
                            }
                        }

                        case "Load" -> {
                            txtInputMenu.setAsInt(false);

                            txtInputMenu.setCategoryNames(List.of("File name"));
                            txtInputMenu.setDefaultValues(List.of("TEST"));
                            txtInputMenu.setCategorySetValues(List.of(
                                    i -> {
                                        try {
                                            loadLevel(i);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                        return null;
                                    }));

                            txtInputMenu.isOpen = true;
                            canPlaceObj = false;

                            GamePanel.camera.update();
                            GamePanel.camera.noUpdate = true;
                        }

                        case "Save" -> {

                            txtInputMenu.setAsInt(false);

                            txtInputMenu.setCategoryNames(List.of("File name"));
                            txtInputMenu.setDefaultValues(List.of("TEST"));
                            txtInputMenu.setCategorySetValues(List.of(
                                    i -> {
                                        saveLevel(i);
                                        return null;
                                    }));

                            txtInputMenu.isOpen = true;
                            canPlaceObj = false;

                            GamePanel.camera.update();
                            GamePanel.camera.noUpdate = true;
                        }

                        default -> {

                            //Images
                            if (b.buttonMessageColor == Color.cyan){
                                rightClickMenu.setX(MouseHandler.getX());
                                rightClickMenu.setY(MouseHandler.getY());
                                rightClickMenu.setButtonText(imageTextureNames.get(b.buttonMessage));
                                rightClickMenu.setButtonImages(imageImages.get(b.buttonMessage));


                                //assigning function to every textures
                                ArrayList<Function<Void, Void>> buttonExec = new ArrayList<>();

                                for (int i = 0; i < imageTextures.get(b.buttonMessage).size(); i++){
                                    int finalI = i;
                                    buttonExec.add(unused -> {
                                        nextObjType = "Image";
                                        nextObjTexture = imageTextures.get(b.buttonMessage).get(finalI);
                                        nextObjFrameCount = imageFrameCount.get(b.buttonMessage).get(finalI);
                                        rightClickMenu.activate();
                                        MouseHandler.resetClicks();
                                        GamePanel.camera.noUpdate = false;
                                        return unused;
                                    });
                                }

                                rightClickMenu.setButtonExec(buttonExec);
                                rightClickMenu.setDisplayedWidth(500);
                                rightClickMenu.setHeight((rightClickMenu.buttonHeight + 10) * imageTextures.get(b.buttonMessage).size() - 10);
                                if (!rightClickMenu.isOpen) {
                                    rightClickMenu.activate();
                                }
                                GamePanel.camera.update();
                                GamePanel.camera.noUpdate = true;
                            }
                            //Platform
                            else{
                                rightClickMenu.setX(MouseHandler.getX());
                                rightClickMenu.setY(MouseHandler.getY());
                                rightClickMenu.setButtonText(platformTextureNames.get(b.buttonMessage));
                                rightClickMenu.setButtonImages(platformImages.get(b.buttonMessage));


                                //assigning function to every textures
                                ArrayList<Function<Void, Void>> buttonExec = new ArrayList<>();

                                for (int i = 0; i < platformTextures.get(b.buttonMessage).size(); i++){
                                    int finalI = i;
                                    buttonExec.add(unused -> {
                                        nextObjType = "Platform";
                                        nextObjTexture = platformTextures.get(b.buttonMessage).get(finalI);
                                        nextObjUtilType = platformUtilTypes.get(b.buttonMessage).get(finalI);
                                        nextObjFrameCount = platformFrameCount.get(b.buttonMessage).get(finalI);
                                        rightClickMenu.activate();
                                        MouseHandler.resetClicks();
                                        GamePanel.camera.noUpdate = false;
                                        return unused;
                                    });
                                }

                                rightClickMenu.setButtonExec(buttonExec);
                                rightClickMenu.setDisplayedWidth(500);
                                rightClickMenu.setHeight((rightClickMenu.buttonHeight + 10) * platformTextures.get(b.buttonMessage).size() - 10);
                                if (!rightClickMenu.isOpen) {
                                    rightClickMenu.activate();
                                }
                                GamePanel.camera.update();
                                GamePanel.camera.noUpdate = true;
                            }
                        }
                    }
                }

            } else if (b.isFocused()){
                b.unfocusedHandler();
            }
        }
    }

    boolean mouseOverButton(Button b){
        //Optimised version of the pointIsIn method of GameObject2D
        if (b.getX() > MouseHandler.getX()) return false;
        if (MouseHandler.getX() > b.getX() + b.getWidth()) return false;
        if (b.getY() > MouseHandler.getY()) return false;
        if (MouseHandler.getY() > b.getY() + b.getHeight()) return false;
        return true;
    }

    public void loadLevel(String lvlName) throws Exception {
        File level = new File("assets/level/"+lvlName+".lvl");

        if (level.exists()){

            Camera c = GamePanel.camera;

            c.isOperational = false;
            objects = new ArrayList<>();
            setX(0);
            setY(0);

            c.nextLevel = lvlName;
            c.level = new Level(lvlName, this, true);
            c.level.loadLevel("level maker settings", new ArrayList<>(), false);
            c.deleteNextLevel();

            c.initialiseGrid(c.level.getSubLvl("main").objectList);
            c.loadVisible();
            c.isOperational = true;

            for (GameObject2D go: c.level.getSubLvl("main").getObjectList()){
                if (!Objects.equals(go.type, "LevelMaker_")){
                    objects.add(go);

                    if (go.name.contains("Background_")){
                        background = go.getBackground();
                    }
                }
            }
        }
    }

    public void saveLevel(String lvlName) {
        try{

            File level = new File("assets/level/"+lvlName+".lvl");
            level.createNewFile();

            FileOutputStream fw = new FileOutputStream(level, false);

            //header
            fw.write("GameIce->VB0.3\nT\n".getBytes());

            //body
            for (GameObject2D go : objects){
                if (go.getType().contains("Player")){
                    fw.write("J".getBytes());
                    fw.write((go.getX() + 32767)/256);
                    fw.write((go.getX() + 32767)%256);
                    fw.write((go.getY() + 32767)/256);
                    fw.write((go.getY() + 32767)%256);
                    fw.write("\n".getBytes());
                }
                else if (go.getType().contains("Platform_")){
                    fw.write("P".getBytes());
                    fw.write(go.getWidth()/256);
                    fw.write(go.getWidth()%256);
                    fw.write(go.getHeight()/256);
                    fw.write(go.getHeight()%256);
                    fw.write((go.getX() + 32767)/256);
                    fw.write((go.getX() + 32767)%256);
                    fw.write((go.getY() + 32767)/256);
                    fw.write((go.getY() + 32767)%256);
                    fw.write(go.getType().charAt(9));
                    fw.write(go.currentAnimation.size() - 1);
                    fw.write((go.getType().substring(10) + "\n").getBytes());
                }
                else if (go.getType().contains("ImageObject_")){
                    fw.write("O".getBytes());
                    fw.write(go.getWidth()/256);
                    fw.write(go.getWidth()%256);
                    fw.write(go.getHeight()/256);
                    fw.write(go.getHeight()%256);
                    fw.write((go.getX() + 32767)/256);
                    fw.write((go.getX() + 32767)%256);
                    fw.write((go.getY() + 32767)/256);
                    fw.write((go.getY() + 32767)%256);
                    fw.write(go.currentAnimation.size() - 1);
                    fw.write((go.getType().substring(12) + "\n").getBytes());
                }
                else if (go.getType().contains("Checkpoint")){
                    fw.write("C".getBytes());
                    fw.write((go.getX() + 32767)/256);
                    fw.write((go.getX() + 32767)%256);
                    fw.write((go.getY() + 32767)/256);
                    fw.write((go.getY() + 32767)%256);
                    fw.write("\n".getBytes());
                }
                else if (go.getType().contains("Background_")){
                    fw.write("A".getBytes());
                    fw.write(background.getWidth()/256);
                    fw.write(background.getWidth()%256);
                    fw.write(background.getHeight()/256);
                    fw.write(background.getHeight()%256);

                    fw.write((background.getX() + 32767)/256);
                    fw.write((background.getX() + 32767)%256);
                    fw.write((background.getY() + 32767)/256);
                    fw.write((background.getY() + 32767)%256);

                    fw.write((int) (background.getZoom() * 100)/256);
                    fw.write((int) (background.getZoom() * 100)%256);
                    fw.write((int) (background.getScrollingSlowness() * 100)/256);
                    fw.write((int) (background.getScrollingSlowness() * 100)%256);

                    if (background.doRepeatX) fw.write(1);
                    else fw.write(0);

                    if (background.doRepeatY) fw.write(1);
                    else fw.write(0);

                    fw.write((background.getType().substring(11) + "\n").getBytes());
                }
            }

            //end
            fw.write("LTTmain\nI".getBytes());
            for (int i = 0; i < 8; i++) fw.write(0);
            fw.write("settingsBg\nBL".getBytes());
            fw.write(307/256);
            fw.write(307%256);
            fw.write(150/256);
            fw.write(150%256);
            fw.write(500/256);
            fw.write(500%256);
            fw.write(400/256);
            fw.write(400%256);
            fw.write((lvlName + ";Reset;base\nFsettings\nFwin\n").getBytes());

            fw.close();
            System.out.println("File /assets/level/"+lvlName+".lvl has been successfully saved" );

            File highScores = new File("assets/HighScores/"+lvlName+".highscore");
            highScores.createNewFile();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
