package cl.view;

import cl.dao.UsuarioDAO;
import cl.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Ventana inicial, valida credenciales y redirige a vista principal con permisos según rol del usuario.
 */
public class VentanaLogin extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnSalir;

    private final UsuarioDAO usuarioDAO;

    public VentanaLogin() {
        this.usuarioDAO = new UsuarioDAO();

        setTitle("Bienvenido a la biblioteca, por favor inicia sesión para continuar");
        setSize(400, 240);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 5, 10));
        JLabel lblTitulo = new JLabel("Acceso al Sistema");
        panelSuperior.add(lblTitulo);
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCentro.setBorder(new EmptyBorder(10, 25, 10, 25));

        txtCorreo = new JTextField();
        txtPassword = new JPasswordField();

        panelCentro.add(new JLabel("Correo:"));
        panelCentro.add(txtCorreo);
        panelCentro.add(new JLabel("Contraseña:"));
        panelCentro.add(txtPassword);
        add(panelCentro, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnIngresar = new JButton("Ingresar");
        btnSalir = new JButton("Salir");

        panelInferior.add(btnIngresar);
        panelInferior.add(btnSalir);
        add(panelInferior, BorderLayout.SOUTH);

        configurarEventos();
    }

    private void configurarEventos() {
        btnIngresar.addActionListener(e -> iniciarSesion());
        btnSalir.addActionListener(e -> System.exit(0));
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (correo.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar correo y contraseña", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.login(correo, pass);

            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Bienvenid@" + usuario.getNombre() + "\nRol: " + usuario.getRol(), "Inicio de Sesión", JOptionPane.INFORMATION_MESSAGE);
                VentanaPrincipal principal = new VentanaPrincipal(usuario);
                principal.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error en SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}