import info.plexteam.obfuscator.PObf;
import info.plexteam.obfuscator.cli.ArgParser;
import info.plexteam.obfuscator.cli.BadArgumentsException;
import info.plexteam.obfuscator.config.Config;
import info.plexteam.obfuscator.gui.GUI;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author jakuubkoo
 * @since 01/01/2020
 */
public class Main {

    public static void main(String[] args)
    {
        Map<String, List<String>> params = new ArgParser().parse(args);

        if(params.containsKey("config"))
        {
            File configFile;
            Config config;

            try
            {
                configFile = new File(params.get("config").get(0));
                config = new Config(configFile);
            }
            catch(Exception e)
            {
                throw new BadArgumentsException("Given config could not be found", e);
            }

            run(new PObf(), config);
        }
        else
        {
//            throw new BadArgumentsException("No config provided");

            try {
                for (UIManager.LookAndFeelInfo look : UIManager.getInstalledLookAndFeels()) {
                    if ("Windows".equalsIgnoreCase(look.getName())) {
                        UIManager.setLookAndFeel(look.getClassName());
                    }
                }
            }catch (Exception e){

            }

            JFrame jFrame = new GUI();
            jFrame.setVisible(true);

        }

        //System.exit(0); // Exit with success code
    }

    public static void run(PObf pObf, Config config)
    {
        try
        {
            pObf.run(config);
        }
        catch(Throwable t)
        {
            throw new RuntimeException("Error while obfuscating", t);
        }
    }

}
