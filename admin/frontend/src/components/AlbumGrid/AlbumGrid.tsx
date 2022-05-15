import React, { useState } from "react";
import { Album } from "../../model/Album";
import { AgGridReact } from "ag-grid-react";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { Button } from "@mui/material";
import UploadAlbumDialog from "../UploadAlbumDialog/UploadAlbumDialog";
import { uploadAlbum } from "../../api/AlbumApi";
import { useSnackbar } from "notistack";
import LoadingScreen from "../LoadingScreen/LoadingScreen";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-alpine-dark.css";

interface Props {
  albums: Album[];
  onFetchAlbums: () => Promise<void>;
}

const AlbumGrid: React.FC<Props> = ({ albums, onFetchAlbums }) => {
  const { enqueueSnackbar } = useSnackbar();
  const [isAddAlbumModalOpen, setIsAddAlbumModalOpen] =
    useState<boolean>(false);
  const [showLoadingScreen, setShowLoadingScreen] = useState<boolean>(false);
  const [columnDefs, setColumnDefs] = useState([
    { field: "id", filter: true },
    { field: "artist", filter: true },
    { field: "albumName", filter: true },
  ]);

  const onUploadAlbum = async (
    artist: string,
    album: string,
    albumZipFile: File
  ) => {
    setIsAddAlbumModalOpen(false);
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
      <Button
        onClick={() => setIsAddAlbumModalOpen(true)}
        startIcon={<CloudUploadIcon />}
      >
        Upload album
      </Button>
      <AgGridReact
        rowData={albums}
        columnDefs={columnDefs} // Column Defs for Columns
        animateRows={true}
      />
      <UploadAlbumDialog
        isOpen={isAddAlbumModalOpen}
        onCancel={() => setIsAddAlbumModalOpen(false)}
        onUpload={(artist, album, albumZipFile) =>
          onUploadAlbum(artist, album, albumZipFile)
        }
      />
      {showLoadingScreen && <LoadingScreen />}
    </div>
  );
};
export default AlbumGrid;
