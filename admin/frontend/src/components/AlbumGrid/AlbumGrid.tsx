import React, { useCallback, useMemo, useState } from "react";
import { Album } from "../../model/Album";
import { AgGridReact } from "ag-grid-react";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { Box, Button } from "@mui/material";
import UploadAlbumDialog from "../UploadAlbumDialog/UploadAlbumDialog";
import AlbumDetailsDialog from "../AlbumDetailsDialog/AlbumDetailsDialog";
import { generateDownloadCodes, uploadAlbum } from "../../api/AlbumApi";
import { useSnackbar } from "notistack";
import LoadingScreen from "../LoadingScreen/LoadingScreen";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-alpine-dark.css";
import { CellClickedEvent } from "ag-grid-community/dist/lib/events";
import GenerateDownloadCodesDialog from "../GenerateDownloadCodesDialog/GenerateDownloadCodesDialog";

interface Props {
  albums: Album[];
  onFetchAlbums: () => Promise<void>;
}

// TODO: resizable columns
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
  const [columnDefs, setColumnDefs] = useState([
    { field: "id", filter: true },
    { field: "artist", filter: true },
    { field: "albumName", filter: true },
  ]);
  const defaultColDef = useMemo(() => ({ sortable: true }), []);

  const cellClickedListener = useCallback((event: CellClickedEvent) => {
    setSelectedAlbum(event.data);
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
      style={{ width: "95%", height: 600, padding: "10px" }}
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
        columnDefs={columnDefs}
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
