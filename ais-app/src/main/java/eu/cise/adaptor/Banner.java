package eu.cise.adaptor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;

/**
 * This class just displays the banner in the standard logs announcing the
 * application name and version.
 *
 * <p>The banner shall be saved in a file called <tt>banner.txt</tt> and placed
 * in the root classpath folder of the project.
 *
 * <p>If the file is not present no banner will be displayed.
 *
 * <p>Example:</p>
 *
 * <p>The following snippet will print a banner found in <tt>classpath:banner.txt</tt>
 * file.</p>
 * <pre>{@code
 *     Banner banner = new Banner();
 *
 *     banner.print();
 * }</pre>
 *
 * @author Gian Carlo Pace &lt;giancarlo.pace@ec.europa.eu&gt;
 */
public class Banner {

    /**
     * Prints the banner from the file banner.txt in the root classpath
     * and the version number in the standard log.
     *
     * <p>The version is taken from the <tt>MANIFEST.MF</tt> file that is
     * enriched by this information from a maven plugin.
     */
    public void print() {
        printBanner();

        printVersion();
    }

    /**
     * Prints the banner from the file banner.txt in the root classpath
     * and the version number in the standard log.
     *
     * <p>If the file is not present doesn't display anything.
     */
    public void printBanner() {
        try {
            if (!Files.exists(getURIBannerFile()))
                return;

            Files.readAllLines(getURIBannerFile()).stream().forEach(System.out::println);

        } catch (IOException e) {
            // If the file have issues the banner won't be displayed.
            // We can deal with that :)
        }
    }

    /**
     * The version is taken from the <tt>MANIFEST.MF</tt> file that is
     * enriched by this information from a maven plugin.
     *
     * <p>If the version is not available it won't be displayed.
     */
    public void printVersion() {
        System.out.println(format("\nVersion: %s\n", getVersion()));
    }

    /**
     * The version is taken from the <tt>MANIFEST.MF</tt> file that is
     * enriched by this information from a maven plugin.
     *
     * <p>If the version is not available it won't be displayed.
     *
     * @return the version of the software as a string if the version is not
     * available  in the <tt>MANIFEST.MF</tt> file, the hard coded string DEV
     */
    public String getVersion() {
        return getManifestVersion() == null ? "DEV" : getManifestVersion();
    }

    // Private /////////////////////////////////////////////////////////////////

    private String getManifestVersion() {
        return this.getClass().getPackage().getImplementationVersion();
    }

    private Path getURIBannerFile() {
        return Optional.ofNullable(getBannerURL()).map((URL url) -> {
            try {
                return url.toURI();
            } catch (URISyntaxException e) {

            }
        }).map();
    }

    private URL getBannerURL() {
        return getClass().getResource("banner.txt");
    }

}
