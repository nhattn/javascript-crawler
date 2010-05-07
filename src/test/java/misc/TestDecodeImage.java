package misc;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.asprise.util.ocr.OCR;

public class TestDecodeImage {
    public static void main(String[] args) throws Exception {
        Base64 b = new Base64();
        String s = "iVBORw0KGgoAAAANSUhEUgAAALAAAAAaCAYAAAAXMNbWAAADmUlEQVR4nO1bMZKrMAzda+0VfpcTpM4BcgBqTkDJEVLspEqbmS2WljLD0GxJS8WvnCGKZEmWYGHGharYenr2s7Bl52Mcxylbtr3aR+zHqq2nw+04fX79exrVthv6qWrr6XQ/v/Q53I5T0ZTT9++PKKBu6KeiKU0+tLHP20iN43B5XNGx8Mb3in0+f3Dcu6EXj7uWuxX7TcDBGZz8GPnL4yoauKIpWdFxPqq2ZgdCE7u3gGNjsVUBf//+kGMW7PK4suJN4W7FfhMw54wKAgZL+aEEWDTlW9vT/fyyKjkfKbGHfpxJJgPjAPl441tj74YebYONe+wLmMLdA5sU8OF2RDNijAAECdsBbvKxBTD/dGBEsU9LauycdUPPZgRsAsOYaD7BqfipfaFY5skBZlRqAaZy98B+E3DV1i+OrCKAA4j5gESwCYKCxLKwd+zYBEkXoGa/bsVP7QvnBvMN5wbySuXugY0KGJq3gLFAJRjQD7UiPWPHcLHFBb8OnuJdMvtKkgLMhLBNKncP7MUFjG3QMYJSDG0sHgLWZt9wci6a8nkSL5pSJTwNvqWvJMNBfvPEYeFuxV5EwEGw2GHqcDuSkygRuWQr4i1giEltW2AbiDufAG1JisO39JWcK2JfUAt3K/ZiAqaC15xg4T6qG3r0ZLq0gCXZD4srZpKtjwbf0lc6PlQ7C3cr9qoC5jIQll3DwFOlMcmEWgQMuWhKd1VbT93QP4v68HfJdkKKb+lrFZGF+yYFDEUZbmSg8DARcwVt6GfpQxy8FZJiSCooktil+J6xa9tZuG9ewHODWwTqRg67STvdz9PlcRWdSL1i12Q/CYZ2/75G9h1H+z7Uwn2Te2DKJIFwJjm1esUOsWIHr9T3Fl74lr7WSoCF+yarEEv5kRDxwtRmP4lgNAt4rew7jvZarIX7LurAVCCaDIxdI0uL5Smxa7OfZJBhm9ijprWyLyZ4LDHEfFq4W7FdBRyucLG3EFhtUJJVqL7cizaLgFOzX2yBaRbgmtmXEon2PYKF+yJvIVJEgL0ooioKVCCBbKyvRrwpAk7Nfli5iHpJt5XsOx93aeyYTwt3K7aLgKkarlaAnB9NNkoRMHYtqsGSvImO+bTgW2Pn6vfcAx0Ldyu2SwaO/RvjdD8/i9uaTKDp6yFgS/aD45Dyb5K/yL4wduyfMNLxt3C3YLMCzpZty5YFnG3XlgWcbdeWBZxt15YFnG3XlgWcbdf2H9DWHBUpwATaAAAAAElFTkSuQmCC";

        ByteArrayInputStream ins = new ByteArrayInputStream(b.decode(s.getBytes()));
        BufferedImage image = ImageIO.read(ins);
        s = new OCR().recognizeEverything(image);
        System.out.println("\n---- RESULTS: ------- \n" + s);

    }
}
