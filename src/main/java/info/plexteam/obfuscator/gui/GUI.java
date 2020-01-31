package info.plexteam.obfuscator.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import info.plexteam.obfuscator.PObf;
import info.plexteam.obfuscator.config.Config;
import info.plexteam.obfuscator.config.TransformerConfig;
import info.plexteam.obfuscator.transformer.transformers.NumberObfuscation;
import info.plexteam.obfuscator.utils.JarFileFilter;
import info.plexteam.obfuscator.utils.Utilities;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

/**
 * @author jakuubkoo
 * @since 04/01/2020
 */
@Getter
public class GUI extends JFrame {

    private JPanel panel;
    private JTabbedPane tabbedPane1;
    private JTabbedPane tabbedPane2;
    private JTextField textField1;
    private JLabel Input;
    private JTextField textField2;
    private JLabel Output;
    private JButton inputButton;
    private JButton outputButton;
    private JButton obfuscateButton;
    private JCheckBox numberEnabledCheckBox;

    private static GUI instance = new GUI();

    public static GUI getInstance(){
        return instance;
    }

    public GUI(){

        JFrame jFrame = new JFrame();

        this.setTitle("PlexObfuscator by jakuubkoo");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel);

        Dimension dimension = this.getToolkit().getScreenSize();

        this.pack();
        this.setSize(500,500);
        this.setLocation((int) dimension.getWidth() / 2 - this.getWidth() / 2, (int) dimension.getHeight() / 2 - this.getHeight() / 2);

        inputButton.addActionListener(e -> {
            String file = Utilities.chooseFile(null, GUI.this, new JarFileFilter());
            if (file != null) {
                textField1.setText(file);
            }
        });
        outputButton.addActionListener(e -> {
            String file = Utilities.chooseFile(null, GUI.this, new JarFileFilter(), true);
            if (file != null) {
                textField2.setText(file);
            }
        });


        obfuscateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Config config = new Config(textField1.getText(), textField2.getText());

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("enabled", numberEnabledCheckBox.isSelected());

                config.transformerConfigs.add(new TransformerConfig("NumberObfuscation", jsonObject));

                config.getJsonObject().addProperty("dependencies", "");

                //Simple debug
//                System.out.println("Debug: " + config.getTransformerConfig("NumberObfuscation").isEnabled());

                PObf.getInstance().run(config);
            }
        });
    }


}


