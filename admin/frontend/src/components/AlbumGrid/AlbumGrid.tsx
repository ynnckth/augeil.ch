import React, { useCallback, useMemo, useState } from "react";
import { Album } from "../../model/Album";
import { AgGridReact } from "ag-grid-react";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { Box, Button } from "@mui/material";
import UploadAlbumDialog from "../UploadAlbumDialog/UploadAlbumDialog";
import AlbumDetailsDialog from "../AlbumDetailsDialog/AlbumDetailsDialog";
import {
  fetchAlbum,
  generateDownloadCodes,
  uploadAlbum,
} from "../../api/AlbumApi";
import { useSnackbar } from "notistack";
import LoadingScreen from "../LoadingScreen/LoadingScreen";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-alpine-dark.css";
import { CellClickedEvent } from "ag-grid-community/dist/lib/events";
import GenerateDownloadCodesDialog from "../GenerateDownloadCodesDialog/GenerateDownloadCodesDialog";
import { ColDef } from "ag-grid-community/dist/lib/entities/colDef";

interface Props {
  albums: Album[];
  onFetchAlbums: () => Promise<void>;
}

const columnDefinitions: ColDef[] = [
  { field: "id", filter: true, resizable: true, initialWidth: 300 },
  { field: "artist", filter: true, resizable: true },
  { field: "albumName", filter: true, resizable: true },
];

const AlbumGrid: React.FC<Props> = ({ albums, onFetchAlbums }) => {
  const { enqueueSnackbar } = useSnackbar();
  const [isAddAlbumDialogOpen, setIsAddAlbumDialogOpen] =
    useState<boolean>(false);
  const [
    isGenerateDownloadCodesDialogOpen,
    setIsGenerateDownloadCodesDialogOpen,
  ] = useState<boolean>(false);

  const [selectedAlbum, setSelectedAlbum] = useState<Album>();
  const [showLoadingScreen, setShowLoadingScreen] = useState<boolean>(false);

  const defaultColDef = useMemo(() => ({ sortable: true }), []);

  const cellClickedListener = useCallback((event: CellClickedEvent) => {
    setShowLoadingScreen(true);
    fetchAlbum(event.data.id)
      .then((albumDetails) => {
        setSelectedAlbum(albumDetails);
        setShowLoadingScreen(false);
      })
      .catch((e) => {
        enqueueSnackbar("Failed to fetch album details", { variant: "error" });
        setShowLoadingScreen(false);
      });
  }, []);

  const onUploadAlbum = async (
    artist: string,
    album: string,
    albumZipFile: File
  ) => {
    setIsAddAlbumDialogOpen(false);
    setShowLoadingScreen(true);
    try {
      await uploadAlbum(artist, album, albumZipFile);
      enqueueSnackbar("Successfully uploaded album", { variant: "success" });
      await onFetchAlbums();
      setShowLoadingScreen(false);
    } catch (e) {
      enqueueSnackbar("Failed to uploaded album", { variant: "error" });
      setShowLoadingScreen(false);
    }
  };

  const onGenerateDownloadCodes = async (
    albumId: string,
    numberOfDownloadCodes: number
  ) => {
    setIsGenerateDownloadCodesDialogOpen(false);
    setShowLoadingScreen(true);
    try {
      await generateDownloadCodes(albumId, numberOfDownloadCodes);
      enqueueSnackbar("Successfully generated download codes", {
        variant: "success",
      });
      await onFetchAlbums();
      setShowLoadingScreen(false);
    } catch (e) {
      enqueueSnackbar("Failed to generate download codes", {
        variant: "error",
      });
      setShowLoadingScreen(false);
    }
  };

  return (
    <div
      className="ag-theme-alpine-dark"
      style={{
        width: "98%",
        height: 600,
        padding: "10px",
        marginTop: "35px",
      }}
    >
      <Box>
        <Button
          onClick={() => setIsAddAlbumDialogOpen(true)}
          startIcon={<CloudUploadIcon />}
        >
          Upload album
        </Button>
        <Button
          onClick={() => setIsGenerateDownloadCodesDialogOpen(true)}
          startIcon={<AddCircleIcon />}
        >
          Generate download codes
        </Button>
      </Box>
      <AgGridReact
        rowData={albums}
        columnDefs={columnDefinitions}
        defaultColDef={defaultColDef}
        onCellClicked={cellClickedListener}
      />
      <UploadAlbumDialog
        isOpen={isAddAlbumDialogOpen}
        onCancel={() => setIsAddAlbumDialogOpen(false)}
        onUpload={onUploadAlbum}
      />
      <AlbumDetailsDialog
        isOpen={!!selectedAlbum}
        onCancel={() => setSelectedAlbum(undefined)}
        selectedAlbum={selectedAlbum}
      />
      <GenerateDownloadCodesDialog
        isOpen={isGenerateDownloadCodesDialogOpen}
        albums={albums}
        onCancel={() => setIsGenerateDownloadCodesDialogOpen(false)}
        onGenerate={onGenerateDownloadCodes}
      />
      {showLoadingScreen && <LoadingScreen />}
    </div>
  );
};
export default AlbumGrid;
