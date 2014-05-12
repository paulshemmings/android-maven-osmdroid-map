package com.razor.osm.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.GEMFFileArchive;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import razor.android.osm.R;

import java.io.File;
import java.io.IOException;

/*
 * https://code.google.com/p/osmdroid/wiki/HowToIncludeInYourProject
 */

public class MapActivity extends Activity {

	private ResourceProxyImpl mResourceProxy;
	private View mMapView;
    private String mGemfArchiveFilename = "archive-filename";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        this.mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);
        return this.mMapView;
    }

    private View buildTileProviderChain() throws IOException
    {
        final Context context = this;
        final Context applicationContext = context.getApplicationContext();
        final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(applicationContext);

        // Create a custom tile source
        final ITileSource tileSource
                = new XYTileSource("Mapnik",
                    ResourceProxy.string.mapnik,
                    1,
                    18,
                    256,
                    ".png",
                    new String[]{"http://tile.openstreetmap.org/"});

        // Create a file cache modular provider
        final TileWriter tileWriter
                = new TileWriter();

        final MapTileFilesystemProvider fileSystemProvider
                = new MapTileFilesystemProvider(registerReceiver, tileSource);

        final File archiveFile
                = new File(mGemfArchiveFilename);

        // Create an archive file modular tile provider
        GEMFFileArchive gemfFileArchive
                = GEMFFileArchive.getGEMFFileArchive(archiveFile); // Requires try/catch

        MapTileFileArchiveProvider fileArchiveProvider
                = new MapTileFileArchiveProvider(registerReceiver, tileSource, new IArchiveFile[] { gemfFileArchive });

        // Create a download modular tile provider
        final NetworkAvailabliltyCheck networkAvailabliltyCheck
                = new NetworkAvailabliltyCheck(context);

        final MapTileDownloader downloaderProvider
                = new MapTileDownloader(tileSource, tileWriter, networkAvailabliltyCheck);

        // Create a custom tile provider array with the custom tile source and the custom tile providers
        final MapTileProviderArray tileProviderArray
                = new MapTileProviderArray(tileSource, registerReceiver, new MapTileModuleProviderBase[] { fileSystemProvider, fileArchiveProvider, downloaderProvider });

        // Create the mapview with the custom tile provider array
        this.mMapView
                = new MapView(context, 256, new DefaultResourceProxyImpl(context),tileProviderArray);

        return this.mMapView;
    }

}
