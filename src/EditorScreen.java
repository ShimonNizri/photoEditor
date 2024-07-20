import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EditorScreen extends JPanel {
    private Window window;
    private Controller controller;
    private SquareFilter squareFilter;

    private JPopupMenu popupMenu;
    private JList<String> optionList;

    private JButton uploadImageButton;
    private JButton changeImageButton;
    private JButton filtersButton;
    private JButton areaSelectionButton;
    private JButton convertToSquareButton;
    private JButton finishedButton;
    private JButton saveImageButton;
    private JButton removeFiltersButton;
    private JButton cancelButton;

    private BufferedImage resultFilter;
    private BufferedImage uploadedImage;

    private boolean isConvert;

    private int originalImageWidth;
    private int originalImageHeight;


    public EditorScreen(Window window){
        this.setLayout(null);
        this.window = window;
        this.setFocusable(true);
        upRepaint();

        addUploadImageButton();
        addFiltersButton();
        addChangeImageButton();
        addAreaSelectionButton();
        addCancelButton();
        addConvertToSquareAndFinishedButton();
        addRemoveFiltersButton();
        addSaveImageButton();


    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setColor(Color.darkGray);
        graphics.fillRect(0,0,window.getWidth(),window.getHeight());
       if (uploadedImage != null || resultFilter != null) {
           BufferedImage image = (resultFilter != null ? resultFilter : uploadedImage);
           graphics.drawImage(image, 0, 50, this.getWidth(), this.getHeight(), this);
       }
       if (squareFilter != null)
         squareFilter.paint(graphics);

    }

    public void upRepaint(){
        new Thread(()->{
            while (true){
                repaint();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void addUploadImageButton(){
        uploadImageButton = new JButton("upload Image"){
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(getForeground());
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        uploadImageButton.setOpaque(false);
        uploadImageButton.setContentAreaFilled(false);
        uploadImageButton.setBorderPainted(false);
        uploadImageButton.setFocusPainted(false);
        uploadImageButton.setBounds(window.getWidth()/2-85,window.getHeight()/2,150,80);

        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp"));

                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    try {
                        uploadedImage = ImageIO.read(fileChooser.getSelectedFile());
                        originalImageWidth = uploadedImage.getWidth();
                        originalImageHeight = uploadedImage.getHeight();
                        BufferedImage b = ImageFilter.copyImage(ImageFilter.resizeImage(uploadedImage,getWidth(),getHeight()-50));//new BufferedImage(getWidth(),getHeight()-50, uploadedImage.getType());
                        uploadedImage = b;
                        uploadImageButton.setVisible(false);
                        filtersButton.setVisible(true);
                        changeImageButton.setVisible(true);
                        areaSelectionButton.setVisible(true);
                        removeFiltersButton.setVisible(true);
                        saveImageButton.setVisible(true);
                    } catch (IOException ex) {

                    }
                }
            }
        });
        this.add(uploadImageButton);
    }

    private void addFiltersButton(){
        filtersButton = new JButton(){
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.drawString("filters",25,20);
                g2.fillRect(10,10,10,2);
                g2.fillRect(10,15,10,2);
                g2.fillRect(10,20,10,2);

            }


        };
        filtersButton.setBounds(0,0,70,30);
        filtersButton.setContentAreaFilled(false);
        filtersButton.setFocusPainted(false);
        filtersButton.setBackground(Color.CYAN);
        filtersButton.setForeground(Color.BLACK);
        addPopupMenu();
        filtersButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popupMenu.show(e.getComponent(),filtersButton.getX(), filtersButton.getY());
            }
        });
        this.add(filtersButton);
        filtersButton.setVisible(false);
    }

    private void addPopupMenu(){
        popupMenu = new JPopupMenu();

        String[] options = {"negative", "mirror", "pixelate", "contrast", "blackWhite", "grayscale","sepia","vignette","solarize","addNoise","lighter","darker"};
         optionList = new JList<>(options);

        JScrollPane scrollPane = new JScrollPane(optionList);
        scrollPane.setPreferredSize(new Dimension(150, 100));

        popupMenu.add(scrollPane);
        optionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedOption = optionList.getSelectedValue();
                if (uploadedImage != null) {
                    if (!isConvert){
                        squareFilter = null;
                    }else {
                        isConvert = false;
                    }

                    if (selectedOption.equals("negative")) {
                        resultFilter = ImageFilter.negative(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("mirror")){
                        resultFilter = ImageFilter.mirror(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("pixelate")){
                        resultFilter = ImageFilter.pixelate(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("contrast")){
                        resultFilter = ImageFilter.contrast(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("blackWhite")){
                        resultFilter = ImageFilter.blackWhite(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("grayscale")){
                        resultFilter = ImageFilter.grayscale(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("sepia")){
                        resultFilter = ImageFilter.sepia(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("vignette")){
                        resultFilter = ImageFilter.vignette(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("solarize")){
                        resultFilter = ImageFilter.solarize(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("addNoise")){
                        resultFilter = ImageFilter.addNoise(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("lighter")){
                        resultFilter = ImageFilter.lighter(uploadedImage,squareFilter,window);
                    }else if (selectedOption.equals("darker")){
                        resultFilter = ImageFilter.darker(uploadedImage,squareFilter,window);
                    }
                    squareFilter = null;
                }
                popupMenu.setVisible(false);
            }
        });
    }

    private void addChangeImageButton(){
        changeImageButton = new JButton(){
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.drawString("change Image",25,20);
                g2.fillRect(10,10,10,2);
                g2.fillRect(10,15,10,2);
                g2.fillRect(10,20,10,2);

            }
        };

        changeImageButton.setBounds(filtersButton.getX()+filtersButton.getWidth(),filtersButton.getY(),110,30);
        changeImageButton.setContentAreaFilled(false);
        changeImageButton.setFocusPainted(false);
        changeImageButton.setBackground(Color.CYAN);
        changeImageButton.setForeground(Color.BLACK);

        changeImageButton.addActionListener((event)-> {
            resultFilter = null;
            uploadedImage = null;
            isConvert = false;
            squareFilter = null;
            filtersButton.setVisible(false);
            changeImageButton.setVisible(false);
            areaSelectionButton.setVisible(false);
            saveImageButton.setVisible(false);
            removeFiltersButton.setVisible(false);
            uploadImageButton.setVisible(true);
        });

        this.add(changeImageButton);
        changeImageButton.setVisible(false);
    }

    private void addAreaSelectionButton(){
        areaSelectionButton = new JButton(){
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.drawString("select Area",25,20);
                g2.fillRect(10,10,10,2);
                g2.fillRect(10,15,10,2);
                g2.fillRect(10,20,10,2);

            }
        };

        areaSelectionButton.setBounds(changeImageButton.getX()+changeImageButton.getWidth(),changeImageButton.getY(),95,30);
        areaSelectionButton.setContentAreaFilled(false);
        areaSelectionButton.setFocusPainted(false);
        areaSelectionButton.setBackground(Color.CYAN);
        areaSelectionButton.setForeground(Color.BLACK);

        areaSelectionButton.addActionListener((event)-> {
            this.controller = new Controller(squareFilter = new SquareFilter(),this);
            this.addMouseListener(controller);
            changeImageButton.setVisible(false);
            filtersButton.setVisible(false);
            areaSelectionButton.setVisible(false);
            saveImageButton.setVisible(false);
            removeFiltersButton.setVisible(false);
            cancelButton.setVisible(true);
            convertToSquareButton.setVisible(true);
            finishedButton.setVisible(true);
        });

        this.add(areaSelectionButton);
        areaSelectionButton.setVisible(false);
    }

    private void addConvertToSquareAndFinishedButton(){
        convertToSquareButton = new JButton(){
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.drawString("convert To Square",25,20);
                g2.fillRect(10,10,10,2);
                g2.fillRect(10,15,10,2);
                g2.fillRect(10,20,10,2);

            }
        };
        convertToSquareButton.setBounds(cancelButton.getX()+cancelButton.getWidth(),0,140,30);
        convertToSquareButton.setContentAreaFilled(false);
        convertToSquareButton.setFocusPainted(false);
        convertToSquareButton.setBackground(Color.CYAN);
        convertToSquareButton.setForeground(Color.BLACK);

        convertToSquareButton.addActionListener((e -> {
            squareFilter.ChangeToSquare();
            isConvert = true;
        }));


        finishedButton = new JButton(){
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.drawString("finish",25,20);
                g2.fillRect(10,10,10,2);
                g2.fillRect(10,15,10,2);
                g2.fillRect(10,20,10,2);

            }
        };
        finishedButton.setBounds(convertToSquareButton.getX()+convertToSquareButton.getWidth(),0,70,30);
        finishedButton.setContentAreaFilled(false);
        finishedButton.setFocusPainted(false);
        finishedButton.setBackground(Color.CYAN);
        finishedButton.setForeground(Color.BLACK);

        finishedButton.addActionListener((e -> {
            if (isConvert) {
                this.setFocusable(false);
                this.removeMouseListener(controller);
                cancelButton.setVisible(false);
                convertToSquareButton.setVisible(false);
                finishedButton.setVisible(false);
                filtersButton.setVisible(true);
                changeImageButton.setVisible(true);
                areaSelectionButton.setVisible(true);
                removeFiltersButton.setVisible(true);
                saveImageButton.setVisible(true);
                squareFilter.stepShowPoint();

            }
        }));



        this.add(convertToSquareButton);
        this.add(finishedButton);

        finishedButton.setVisible(false);
        convertToSquareButton.setVisible(false);

    }

    public void setConvert(boolean newConvert) {
        this.isConvert = newConvert;
    }

    private void addSaveImageButton() {
        saveImageButton = new JButton(){
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.drawString("save Image",25,20);
                g2.fillRect(10,10,10,2);
                g2.fillRect(10,15,10,2);
                g2.fillRect(10,20,10,2);

            }
        };
        saveImageButton.setBounds(removeFiltersButton.getX()+removeFiltersButton.getWidth(),removeFiltersButton.getY(),95,30);
        saveImageButton.setContentAreaFilled(false);
        saveImageButton.setFocusPainted(false);
        saveImageButton.setBackground(Color.CYAN);
        saveImageButton.setForeground(Color.BLACK);

        saveImageButton.addActionListener(e -> {
            if (resultFilter != null) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("בחר תמונה לשמירה");

                FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Image", "png");
                FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Image", "jpg", "jpeg");
                FileNameExtensionFilter bmpFilter = new FileNameExtensionFilter("BMP Image", "bmp");
                FileNameExtensionFilter gifFilter = new FileNameExtensionFilter("GIF Image", "gif");

                fileChooser.addChoosableFileFilter(pngFilter);
                fileChooser.addChoosableFileFilter(jpgFilter);
                fileChooser.addChoosableFileFilter(bmpFilter);
                fileChooser.addChoosableFileFilter(gifFilter);
                fileChooser.setFileFilter(pngFilter);

                int userSelection = fileChooser.showSaveDialog(null);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();

                    String format = ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];

                    if (!fileToSave.getAbsolutePath().toLowerCase().endsWith("." + format)) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + "." + format);
                    }

                    BufferedImage image = ImageFilter.resizeImage(resultFilter, originalImageWidth, originalImageHeight);

                    try {
                        ImageIO.write(image, format, fileToSave);
                        JOptionPane.showMessageDialog(null, "התמונה נשמרה בהצלחה: " + fileToSave.getAbsolutePath(), "שמירת תמונה", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException k) {
                        JOptionPane.showMessageDialog(null, "שגיאה בשמירת התמונה: " + k.getMessage(), "שגיאה", JOptionPane.ERROR_MESSAGE);
                    }
                }


            }

        });
        this.add(saveImageButton);
        saveImageButton.setVisible(false);
    }
    private void addRemoveFiltersButton(){
        removeFiltersButton = new JButton(){
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.drawString("remove filters",25,20);
                g2.fillRect(10,10,10,2);
                g2.fillRect(10,15,10,2);
                g2.fillRect(10,20,10,2);

            }
        };
        removeFiltersButton.setBounds(areaSelectionButton.getX()+areaSelectionButton.getWidth(),areaSelectionButton.getY(),110,30);
        removeFiltersButton.setContentAreaFilled(false);
        removeFiltersButton.setFocusPainted(false);
        removeFiltersButton.setBackground(Color.CYAN);
        removeFiltersButton.setForeground(Color.BLACK);

        removeFiltersButton.addActionListener(e -> {
            resultFilter = null;
            squareFilter = null;

        });
        this.add(removeFiltersButton);
        removeFiltersButton.setVisible(false);
    }

    private void addCancelButton(){
        cancelButton = new JButton(){
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.white);
                g2.drawString("cancel",25,20);
                g2.fillRect(10,10,10,2);
                g2.fillRect(10,15,10,2);
                g2.fillRect(10,20,10,2);

            }
        };
        cancelButton.setBounds(0,0,75,30);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setBackground(Color.CYAN);
        cancelButton.setForeground(Color.BLACK);

        cancelButton.addActionListener(e -> {
            this.setFocusable(false);
            this.removeMouseListener(controller);
            squareFilter = null;
            convertToSquareButton.setVisible(false);
            cancelButton.setVisible(false);
            finishedButton.setVisible(false);
            filtersButton.setVisible(true);
            changeImageButton.setVisible(true);
            areaSelectionButton.setVisible(true);
            removeFiltersButton.setVisible(true);
            saveImageButton.setVisible(true);
        });
        this.add(cancelButton);
        cancelButton.setVisible(false);
    }
}