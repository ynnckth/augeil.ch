import React, { useCallback, useMemo, useState } from "react";
import { Album } from "../../model/Album";
import { AgGridReact } from "ag-grid-react";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { Box, Button } from "@mui/material";
import UploadAlbumDialog from "../UploadAlbumDialog/UploadAlbumDialog";
import AlbumDetailsDialog from "../AlbumDetailsDialog/AlbumDetailsDialog";
import { uploadAlbum } from "../../api/AlbumApi";
import { useSnackbar } from "notistack";
import LoadingScreen from "../LoadingScreen/LoadingScreen";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-alpine-dark.css";
import { CellClickedEvent } from "ag-grid-community/dist/lib/events";

interface Props {
  albums: Album[];
  onFetchAlbums: () => Promise<void>;
}

const AlbumGrid: React.FC<Props> = ({ albums, onFetchAlbums }) => {
  const { enqueueSnackbar } = useSnackbar();
  const [isAddAlbumDialogOpen, setIsAddAlbumDialogOpen] =
    useState<boolean>(false);
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
      enqueueSnackbar("Successfully uploaded album", { variant: "error" });
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
        {/* TODO: add a dialog for download code generation */}
        <Button onClick={() => {}} startIcon={<AddCircleIcon />}>
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
        onUpload={(artist, album, albumZipFile) =>
          onUploadAlbum(artist, album, albumZipFile)
        }
      />
      <AlbumDetailsDialog
        isOpen={!!selectedAlbum}
        onCancel={() => setSelectedAlbum(undefined)}
        selectedAlbum={selectedAlbum}
      />
      {showLoadingScreen && <LoadingScreen />}
    </div>
  );
};
export default AlbumGrid;
