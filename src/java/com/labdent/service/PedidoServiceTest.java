package com.labdent.service;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionEstadoDAO;
import com.labdent.exception.InvalidStateTransitionException;
import com.labdent.exception.PedidoNotFoundException;
import com.labdent.model.Pedido;
import com.labdent.model.TransicionEstado;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PedidoService
 * Implementa TDD (Test-Driven Development)
 */
@DisplayName("Tests de PedidoService")
class PedidoServiceTest {
    
    @Mock
    private PedidoDAO pedidoDAO;
    
    @Mock
    private TransicionEstadoDAO transicionDAO;
    
    @Mock
    private NotificacionService notificacionService;
    
    private PedidoService pedidoService;
    private AutoCloseable closeable;
    
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        pedidoService = new PedidoService(pedidoDAO, transicionDAO, notificacionService);
    }
    
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
    
    @Test
    @DisplayName("Debe obtener un pedido por ID correctamente")
    void testObtenerPedidoPorId() {
        // Arrange
        int pedidoId = 1;
        Pedido pedidoEsperado = new Pedido();
        pedidoEsperado.setId(pedidoId);
        pedidoEsperado.setCodigoUnico("LAB-001");
        pedidoEsperado.setEstadoActual("RECEPCION");
        
        when(pedidoDAO.obtenerPorId(pedidoId)).thenReturn(pedidoEsperado);
        
        // Act
        Optional<Pedido> resultado = pedidoService.obtenerPedidoPorId(pedidoId);
        
        // Assert
        Assertions.assertTrue(resultado.isPresent(), "El pedido debería estar presente");
        Assertions.assertEquals("LAB-001", resultado.get().getCodigoUnico(), 
                              "El código único debería coincidir");
        verify(pedidoDAO, times(1)).obtenerPorId(pedidoId);
    }
    
    @Test
    @DisplayName("Debe retornar Optional.empty cuando el pedido no existe")
    void testObtenerPedidoNoExistente() {
        // Arrange
        int pedidoId = 999;
        when(pedidoDAO.obtenerPorId(pedidoId)).thenReturn(null);
        
        // Act
        Optional<Pedido> resultado = pedidoService.obtenerPedidoPorId(pedidoId);
        
        // Assert
        Assertions.assertTrue(resultado.isEmpty(), "El resultado debería estar vacío");
        verify(pedidoDAO, times(1)).obtenerPorId(pedidoId);
    }
    
    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con ID igual a cero")
    void testObtenerPedidoConIdCero() {
        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.obtenerPedidoPorId(0),
            "Debería lanzar IllegalArgumentException con ID 0"
        );
        
        Assertions.assertTrue(exception.getMessage().contains("mayor a 0"));
        verify(pedidoDAO, never()).obtenerPorId(anyInt());
    }
    
    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con ID negativo")
    void testObtenerPedidoConIdNegativo() {
        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.obtenerPedidoPorId(-1),
            "Debería lanzar IllegalArgumentException con ID negativo"
        );
        
        Assertions.assertTrue(exception.getMessage().contains("mayor a 0"));
        verify(pedidoDAO, never()).obtenerPorId(anyInt());
    }
    
    @Test
    @DisplayName("Debe actualizar estado correctamente con transición válida")
    void testActualizarEstadoTransicionValida() {
        // Arrange
        int pedidoId = 1;
        int usuarioId = 5;
        String estadoInicial = "RECEPCION";
        String estadoNuevo = "PARALELIZADO";
        String observaciones = "Proceso completado correctamente";
        
        Pedido pedido = crearPedidoMock(pedidoId, estadoInicial);
        
        when(pedidoDAO.obtenerPorId(pedidoId)).thenReturn(pedido);
        when(transicionDAO.obtenerUltimaTransicion(pedidoId)).thenReturn(null);
        when(transicionDAO.registrarTransicion(any(TransicionEstado.class))).thenReturn(true);
        when(pedidoDAO.actualizarEstado(pedidoId, estadoNuevo, usuarioId)).thenReturn(true);
        doNothing().when(notificacionService).notificarCambioEstado(any(Pedido.class), eq(estadoNuevo));
        
        // Act
        boolean resultado = pedidoService.actualizarEstadoPedido(
            pedidoId, estadoNuevo, usuarioId, observaciones
        );
        
        // Assert
        Assertions.assertTrue(resultado, "La actualización debería ser exitosa");
        verify(pedidoDAO, times(1)).obtenerPorId(pedidoId);
        verify(transicionDAO, times(1)).registrarTransicion(any(TransicionEstado.class));
        verify(pedidoDAO, times(1)).actualizarEstado(pedidoId, estadoNuevo, usuarioId);
        verify(notificacionService, times(1)).notificarCambioEstado(any(Pedido.class), eq(estadoNuevo));
    }
    
    @Test
    @DisplayName("Debe lanzar InvalidStateTransitionException con transición inválida")
    void testActualizarEstadoTransicionInvalida() {
        // Arrange
        int pedidoId = 1;
        String estadoInicial = "RECEPCION";
        String estadoInvalido = "CERAMICA"; // Salto inválido
        
        Pedido pedido = crearPedidoMock(pedidoId, estadoInicial);
        
        when(pedidoDAO.obtenerPorId(pedidoId)).thenReturn(pedido);
        
        // Act & Assert
        InvalidStateTransitionException exception = Assertions.assertThrows(
            InvalidStateTransitionException.class,
            () -> pedidoService.actualizarEstadoPedido(pedidoId, estadoInvalido, 5, "Intento inválido"),
            "Debería lanzar InvalidStateTransitionException"
        );
        
        Assertions.assertEquals(estadoInicial, exception.getCurrentState());
        Assertions.assertEquals(estadoInvalido, exception.getTargetState());
        
        verify(pedidoDAO, times(1)).obtenerPorId(pedidoId);
        verify(transicionDAO, never()).registrarTransicion(any());
        verify(pedidoDAO, never()).actualizarEstado(anyInt(), anyString(), anyInt());
        verify(notificacionService, never()).notificarCambioEstado(any(), anyString());
    }
    
    @Test
    @DisplayName("Debe lanzar PedidoNotFoundException cuando el pedido no existe")
    void testActualizarEstadoPedidoNoExiste() {
        // Arrange
        int pedidoId = 999;
        when(pedidoDAO.obtenerPorId(pedidoId)).thenReturn(null);
        
        // Act & Assert
        PedidoNotFoundException exception = Assertions.assertThrows(
            PedidoNotFoundException.class,
            () -> pedidoService.actualizarEstadoPedido(pedidoId, "PARALELIZADO", 5, ""),
            "Debería lanzar PedidoNotFoundException"
        );
        
        Assertions.assertTrue(exception.getMessage().contains(String.valueOf(pedidoId)));
        
        verify(pedidoDAO, times(1)).obtenerPorId(pedidoId);
        verify(transicionDAO, never()).registrarTransicion(any());
        verify(pedidoDAO, never()).actualizarEstado(anyInt(), anyString(), anyInt());
    }
    
    @Test
    @DisplayName("Debe permitir transición de PRODUCCION_CAM a CERAMICA")
    void testTransicionProduccionCamACeramica() {
        // Arrange
        int pedidoId = 1;
        Pedido pedido = crearPedidoMock(pedidoId, "PRODUCCION_CAM");
        
        when(pedidoDAO.obtenerPorId(pedidoId)).thenReturn(pedido);
        when(transicionDAO.registrarTransicion(any())).thenReturn(true);
        when(pedidoDAO.actualizarEstado(anyInt(), anyString(), anyInt())).thenReturn(true);
        
        // Act
        boolean resultado = pedidoService.actualizarEstadoPedido(
            pedidoId, "CERAMICA", 5, "Requiere cerámica"
        );
        
        // Assert
        Assertions.assertTrue(resultado, "Debería permitir la transición a CERAMICA");
    }
    
    @Test
    @DisplayName("Debe permitir salto de PRODUCCION_CAM a CONTROL_CALIDAD")
    void testSaltoProduccionCamAControlCalidad() {
        // Arrange
        int pedidoId = 1;
        Pedido pedido = crearPedidoMock(pedidoId, "PRODUCCION_CAM");
        
        when(pedidoDAO.obtenerPorId(pedidoId)).thenReturn(pedido);
        when(transicionDAO.registrarTransicion(any())).thenReturn(true);
        when(pedidoDAO.actualizarEstado(anyInt(), anyString(), anyInt())).thenReturn(true);
        
        // Act
        boolean resultado = pedidoService.actualizarEstadoPedido(
            pedidoId, "CONTROL_CALIDAD", 5, "Sin cerámica"
        );
        
        // Assert
        Assertions.assertTrue(resultado, "Debería permitir saltar CERAMICA");
    }
    
    @Test
    @DisplayName("Debe obtener siguientes estados disponibles para RECEPCION")
    void testObtenerSiguientesEstadosDesdeRecepcion() {
        // Act
        List<String> estados = pedidoService.obtenerSiguientesEstadosDisponibles("RECEPCION");
        
        // Assert
        Assertions.assertNotNull(estados, "La lista no debería ser null");
        Assertions.assertEquals(1, estados.size(), "Debería haber exactamente 1 estado siguiente");
        Assertions.assertTrue(estados.contains("PARALELIZADO"), "Debería contener PARALELIZADO");
    }
    
    @Test
    @DisplayName("Debe obtener dos siguientes estados para PRODUCCION_CAM")
    void testObtenerSiguientesEstadosDesdeProduccionCam() {
        // Act
        List<String> estados = pedidoService.obtenerSiguientesEstadosDisponibles("PRODUCCION_CAM");
        
        // Assert
        Assertions.assertNotNull(estados, "La lista no debería ser null");
        Assertions.assertEquals(2, estados.size(), "Debería haber exactamente 2 estados siguientes");
        Assertions.assertTrue(estados.contains("CERAMICA"), "Debería contener CERAMICA");
        Assertions.assertTrue(estados.contains("CONTROL_CALIDAD"), "Debería contener CONTROL_CALIDAD");
    }
    
    @Test
    @DisplayName("Debe retornar lista vacía para estado sin siguientes estados")
    void testObtenerSiguientesEstadosSinSiguientes() {
        // Act
        List<String> estados = pedidoService.obtenerSiguientesEstadosDisponibles("ESTADO_INVALIDO");
        
        // Assert
        Assertions.assertNotNull(estados, "La lista no debería ser null");
        Assertions.assertTrue(estados.isEmpty(), "La lista debería estar vacía");
    }
    
    @Test
    @DisplayName("Debe obtener pedidos por estado correctamente")
    void testObtenerPedidosPorEstado() {
        // Arrange
        String estado = "DISENO_CAD";
        List<Pedido> pedidosEsperados = List.of(
            crearPedidoMock(1, estado),
            crearPedidoMock(2, estado)
        );
        
        when(pedidoDAO.listarPorEstado(estado)).thenReturn(pedidosEsperados);
        
        // Act
        List<Pedido> resultado = pedidoService.obtenerPedidosPorEstado(estado);
        
        // Assert
        Assertions.assertNotNull(resultado, "La lista no debería ser null");
        Assertions.assertEquals(2, resultado.size(), "Debería retornar 2 pedidos");
        verify(pedidoDAO, times(1)).listarPorEstado(estado);
    }
    
    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con estado vacío")
    void testObtenerPedidosConEstadoVacio() {
        // Act & Assert
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.obtenerPedidosPorEstado(""),
            "Debería lanzar IllegalArgumentException con estado vacío"
        );
        
        verify(pedidoDAO, never()).listarPorEstado(anyString());
    }
    
    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con estado null")
    void testObtenerPedidosConEstadoNull() {
        // Act & Assert
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> pedidoService.obtenerPedidosPorEstado(null),
            "Debería lanzar IllegalArgumentException con estado null"
        );
        
        verify(pedidoDAO, never()).listarPorEstado(anyString());
    }
    
    // Métodos auxiliares
    
    private Pedido crearPedidoMock(int id, String estadoActual) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setCodigoUnico("LAB-" + id);
        pedido.setEstadoActual(estadoActual);
        pedido.setUsuarioId(10);
        pedido.setOdontologoId(2);
        pedido.setNombrePaciente("Paciente Test");
        pedido.setTipoProtesis("Corona");
        pedido.setMaterial("Zirconia");
        pedido.setFechaIngreso(new Timestamp(System.currentTimeMillis()));
        pedido.setFechaCompromiso(new Date(System.currentTimeMillis() + 86400000));
        return pedido;
    }
}