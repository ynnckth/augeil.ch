import React from "react";
import { Album } from "../../model/Album";
import { AgGridReact } from "ag-grid-react";
import { ColDef } from "ag-grid-community/dist/lib/entities/colDef";

interface Props {
  album: Album;
}

const columnDefinitions: ColDef[] = [
  { field: "code", filter: true, resizable: true },
  { field: "availableDownloads", filter: true, resizable: true },
];

const AlbumDetailsGrid: React.FC<Props> = ({ album }) => {
  return (
    <div
      className="ag-theme-alpine-dark"
      style={{ width: "450px", height: "300px" }}
    >
      <AgGridReact
        rowData={album.downloadCodes}
        columnDefs={columnDefinitions}
        enableCellTextSelection={true}
      />
    </div>
  );
};
export default AlbumDetailsGrid;
