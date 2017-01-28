package smartstation.gui;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import smartstation.logging.SmartLogger;
import smartstation.util.Constants;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;

public class LoginPage {
	// Constants
	

	private JFrame loginPage;
	private SmartLogger logger;
	private JTextField textField;
	private JButton btnLogin;
	private JButton btnCancel;
	private JPanel panel;
	private JPasswordField passwordField;

	public LoginPage() {
		logger = new SmartLogger();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// Log default look and feel
			logger.getLogger().warning("Unable to get system look and feel");
		}
		loginPage = new JFrame("Login");
		loginPage.setResizable(false);
		// Set Icon for JFrame
		ImageIcon icon = new ImageIcon("icon/car_32.png");
		loginPage.setIconImage(icon.getImage());
		
		loginPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginPage.getContentPane().setLayout(null);

		panel = new JPanel();
		panel.setBounds(10, 11, 414, 123);
		loginPage.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(10, 11, 63, 14);
		panel.add(lblUsername);

		textField = new JTextField();
		textField.setBounds(92, 8, 281, 20);
		panel.add(textField);
		textField.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 55, 63, 20);
		panel.add(lblPassword);

		btnLogin = new JButton("Login");
		btnLogin.setBounds(119, 89, 89, 23);
		panel.add(btnLogin);
		// Action when Login btn is pressed
		btnLogin.addActionListener(ev -> {
			String username = textField.getText();
			String password = new String(passwordField.getPassword());
			if (validate(username, password)) {
				if (authenticate(username, password, Constants.DB_NAME)) {
					// Invoke the main page
					new MainPage(username, password);
					loginPage.dispose();
				} else {
					JOptionPane.showMessageDialog(loginPage,
							"Access Denied !!!");
				}
			} else {
				JOptionPane.showMessageDialog(loginPage,
						"Invalid Credentials !!!");
			}
		});

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(243, 89, 89, 23);
		panel.add(btnCancel);
		// Action to be performed when Cancel btn id pressed
		btnCancel.addActionListener(ev -> {
			loginPage.dispose();
		});

		passwordField = new JPasswordField();
		passwordField.setBounds(92, 55, 281, 20);
		panel.add(passwordField);
		loginPage.setBounds(0, 0, 450, 200);
		loginPage.setLocationRelativeTo(null);
		loginPage.setVisible(true);
	}

	public static void main(String[] args) {
		new LoginPage();
	}

	/**
	 * Basic validations of username & password Currently only checks if empty
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean validate(final String username, final String password) {
		if ( // Check if the username and password are provided
		username != null && password != null &&
		// Check if the username and password are not empty
				!username.equals("") && !password.equals("")) {
			return true;
		}

		return false;
	}

	/**
	 * Method to check if the credentials are valid
	 * 
	 * @param username
	 * @param password
	 * @param database
	 * @return
	 */
	public boolean authenticate(final String username, final String password,
			final String database) {
		logger.getLogger().info("Trying to authenticate user " + username + " for the database " + database);
		try {
			Class.forName(Constants.MYSQL_DRIVER);
		} catch (ClassNotFoundException e1) {
			// System.out.println("MySql Database driver not found!!!");
			logger.getLogger().severe("MySql driver not found !!!");
		}
		// Setup the connection with the DB with given credentials
		try {
			Connection connect = DriverManager
					.getConnection("jdbc:mysql://localhost/" + database + "?"
							+ "user=" + username + "&password=" + password);
			connect.close();
		} catch (Exception e) {
			return false;
		}
		logger.getLogger().info("User " + username + "authenticated for database " + database);
		return true;
	}

}
