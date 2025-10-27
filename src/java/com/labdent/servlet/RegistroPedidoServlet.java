package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionDAO;
import com.labdent.model.Pedido;
import com.labdent.model.TransicionEstado;
import com.labdent.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/registro-pedido")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class RegistroPedidoServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;
    private TransicionDAO transicionDAO;

    @Override
    public void init() {
        pedidoDAO = new PedidoDAO();
        transicionDAO = new TransicionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }

        request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            // Crear objeto Pedido
            Pedido pedido = new Pedido();
            pedido.setOdontologoId(usuario.getId());
            pedido.setNombrePaciente(request.getParameter("nombrePaciente"));
            pedido.setPiezasDentales(request.getParameter("piezasDentales"));
            pedido.setTipoProtesis(request.getParameter("tipoProtesis"));
            pedido.setMaterial(request.getParameter("material"));
            pedido.setColorShade(request.getParameter("colorShade"));
            pedido.setFechaCompromiso(Date.valueOf(request.getParameter("fechaCompromiso")));
            
            String prioridad = request.getParameter("prioridad");
            pedido.setPrioridad(prioridad != null ? prioridad : "NORMAL");
            
            pedido.setObservaciones(request.getParameter("observaciones"));

            // Manejo de archivo adjunto
            Part filePart = request.getPart("archivoAdjunto");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = extractFileName(filePart);
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                
                String filePath = uploadPath + File.separator + System.currentTimeMillis() + "_" + fileName;
                filePart.write(filePath);
                pedido.setArchivoAdjunto(fileName);
            }

            // Registrar pedido
            if (pedidoDAO.registrar(pedido)) {
                // Registrar transición inicial
                TransicionEstado transicion = new TransicionEstado();
                transicion.setPedidoId(pedido.getId());
                transicion.setEstadoAnterior(null);
                transicion.setEstadoNuevo("RECEPCION");
                transicion.setUsuarioId(usuario.getId());
                transicion.setObservaciones("Pedido registrado por " + usuario.getNombreCompleto());
                transicion.setChecklistCompletado(true);
                transicionDAO.registrar(transicion);

                // Mensaje de éxito
                request.setAttribute("success", "Pedido registrado exitosamente");
                request.setAttribute("codigoPedido", pedido.getCodigoUnico());
                request.setAttribute("mensaje", "Su código de seguimiento es: " + pedido.getCodigoUnico() + 
                                               ". Guárdelo para consultar el estado de su pedido.");
                request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Error al registrar el pedido. Intente nuevamente.");
                request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }
}