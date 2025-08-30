export interface Historial { 
  idHistorial?: number;  
  idPunto?: number;           
  fecha?: string;            
  descripcion?: string;
  imagenes?: ImagenHistorial[];     
  editando?: boolean;  

  nuevasImagenes?: File[];
  nuevasPreview?: string[];
}

export interface ImagenHistorial {
  id?: number;
  nombre: string;
   pendienteEliminar?: boolean;
}
