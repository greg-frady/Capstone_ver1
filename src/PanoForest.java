import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.io.File;
import java.util.Arrays;

//import java.io.FileInputStream;

public class PanoForest extends Component {

    public BufferedImage img;
    //private PanoCamRig pCamRig;

    public PanoForest() {
    }

    public void paint(Graphics g)
    {
        g.drawImage(img, 0, 0, null);
    }


    public Dimension getPreferredSize()
    {
		if (img == null) {
             return new Dimension(100,100);
        } else {
           return new Dimension(img.getWidth(null), img.getHeight(null));
		}
    }


	public BufferedImage loadImage(File f)
	{
		BufferedImage image;
		try {
			image = ImageIO.read(f);
		}catch (IOException e) {
			System.err.println("Error reading file: " + f);
			image = null;
		}
		return image;
	}

	public void saveImage(BufferedImage bImage, File file)
	{
		try {
			ImageIO.write(bImage, "png", file);
		}catch (IOException e) {
		}
	}

	public BufferedImage[] folderImport(File folder)
	{
		if(folder.isDirectory())
		{
			//public String[] list()
			String files[] = folder.list();
			//java.util.Arrays.sort(myArray);
			Arrays.sort(files);
			int length = files.length;
			BufferedImage[] bi = new BufferedImage[length];
			for(int i =0; i < length; i++) //elements of list
			{
				bi[i] = loadImage(new File(files[i]));
			}
			return bi;
		}
		else return null;
	}

	public void setImage(BufferedImage b)
	{
		img = b;
	}

    //public void setPanoCamRig(PanoCamRig pCamRig1){pCamRig = pCamRig1; }

//---------------------------------------------------------------------------------
    public static void main(String[] args) //throws IOException
    {
        JFrame f = new JFrame("Load Image Sample");

        f.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        PanoForest pf = new PanoForest();

        //TODO make an argument to set the "project directory"
        //File projectDir = new File(args[0]);
        // TODO: try/catch the file captures
        // TODO: clean up file handling
        BufferedImage centralImg = pf.loadImage(new File(".\\Images\\"+args[0]));//".\\Images\\forestUsingTreeHR_noShadows_0.png"));
        BufferedImage matteImg = pf.loadImage(new File(".\\Images\\"+args[1]));
        BufferedImage ZDepthImg = pf.loadImage(new File(".\\Images\\"+args[2]));

		//when importing matte image, use TYPE_BYTE_BINARY, for byte packing

		File circleImagesFolder = new File(".\\Images\\CirclePanos\\");
		BufferedImage circleImages[];
		if(circleImagesFolder.isDirectory())
        {
            circleImages = pf.folderImport(circleImagesFolder);
        }
		else { circleImages = null; }

        PanoCamRig pCamRig;
        if ((circleImages != null) && (centralImg != null) && (matteImg != null)) {
            //Instantiate PanoCamRig object
            //pf.setPanoCamRig(new PanoCamRig(centralImg, circleImages, matteImg, ZDepthImg)); //if PanoCamRig is member
            pCamRig = new PanoCamRig(centralImg, circleImages, matteImg, ZDepthImg);

        //TODO double check this shit


        BufferedImage filtZdepth = pCamRig.generateZDepth();
        BufferedImage outputImg = pCamRig.ImageFromDepth(filtZdepth);

        pf.saveImage(outputImg, new File(".\\OutputImage.png"));

        f.add(pf);
		f.pack();
        f.setVisible(true);
        }
        else System.out.println("Could not create PanoCamRig object.");
    }

}