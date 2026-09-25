package com.kaisernova.validador.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;


public class CedulaConstraintValidator implements ConstraintValidator<Cedula, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return true;//ValidadorCedulaUtil.validaIdentificacion(value);
	}

}

class ValidadorCedulaUtil {
	public static boolean validaIdentificacion(String identificacion) {
		if(Objects.isNull(identificacion)) {
			return false;
		}
		boolean resultadoVal = false;
		String cedula = "";
		int vec3;
		if (validarCadenaNoNumerica(identificacion)) {
			resultadoVal = false;
		}
		if (identificacion.length() != 10) {
			resultadoVal = false;
		} else {
			cedula = identificacion.substring(0, 10);
			vec3 = Integer.parseInt(identificacion.substring(2, 3));

			if ((vec3 >= 0 && vec3 <= 5)) {
				resultadoVal = validaCedula(cedula);

			} else if (vec3 == 6) {
				resultadoVal = validaTercero6(cedula);

			} else if (vec3 == 9) {
				resultadoVal = validaTercero9(cedula);
			}
		}

		return resultadoVal;
	}

	private static boolean validaCedula(String cedula) {
		boolean resultado = false;
		int digito;
		int digito10 = Integer.parseInt(cedula.substring(9));
		int suma = 0;
		int multi;
		int cociente;
		int decena;
		int verificador;

		int cont = 0;
		int contPos = 0;
		while (contPos < 9) {
			contPos = 2 * cont + 1;
			digito = Integer.parseInt(cedula.substring(contPos - 1, contPos));
			multi = digito * 2;
			if (multi >= 10) {
				multi = 1 + (multi % 10);
			}
			suma += multi;
			cont++;
		}

		cont = 1;
		contPos = 1;
		while (contPos < 8) {
			contPos = 2 * cont;
			digito = Integer.parseInt(cedula.substring(contPos - 1, contPos));
			suma += digito;
			cont++;
		}

		cociente = suma / 10;
		decena = (cociente + 1) * 10;
		verificador = decena - suma;

		if (verificador == 10) {
			verificador = 0;
		}

		if (verificador == digito10) {
			resultado = true;
		}
		return resultado;
	}

	private static boolean validaTercero6(String cedula) {
		int digito1 = Integer.parseInt(cedula.substring(0, 1)) * 3;
		int digito2 = Integer.parseInt(cedula.substring(1, 2)) * 2;
		int digito3 = Integer.parseInt(cedula.substring(2, 3)) * 7;
		int digito4 = Integer.parseInt(cedula.substring(3, 4)) * 6;
		int digito5 = Integer.parseInt(cedula.substring(4, 5)) * 5;
		int digito6 = Integer.parseInt(cedula.substring(5, 6)) * 4;
		int digito7 = Integer.parseInt(cedula.substring(6, 7)) * 3;
		int digito8 = Integer.parseInt(cedula.substring(7, 8)) * 2;
		int verificador;
		int verificador2;
		int suma;
		int suma2;
		boolean respuesta = false;

		digito1 = sumaDigito(digito1);
		digito2 = sumaDigito(digito2);
		digito3 = sumaDigito(digito3);
		digito4 = sumaDigito(digito4);
		digito5 = sumaDigito(digito5);
		digito6 = sumaDigito(digito6);
		digito7 = sumaDigito(digito7);
		digito8 = sumaDigito(digito8);

		suma = digito1 + digito2 + digito3 + digito4 + digito5 + digito6 + digito7 + digito8;
		suma2 = Integer.parseInt(cedula.substring(0, 1)) * 3 + Integer.parseInt(cedula.substring(1, 2)) * 2
				+ Integer.parseInt(cedula.substring(2, 3)) * 7 + Integer.parseInt(cedula.substring(3, 4)) * 6
				+ Integer.parseInt(cedula.substring(4, 5)) * 5 + Integer.parseInt(cedula.substring(5, 6)) * 4
				+ Integer.parseInt(cedula.substring(6, 7)) * 3 + Integer.parseInt(cedula.substring(7, 8)) * 2;

		verificador = 11 - (suma % 11);
		verificador2 = 11 - (suma2 % 11);
		if (verificador == 11 || verificador2 == 11) {
			verificador = 0;
		}

		if (verificador == Integer.parseInt(cedula.substring(8, 9))
				|| verificador2 == Integer.parseInt(cedula.substring(8, 9))) {
			respuesta = true;
		} else {
			respuesta = validaCedula(cedula);
		}
		return respuesta;
	}

	private static int sumaDigito(Integer digito) {
		int suma = digito;
		int valor1 = 0;
		int valor2 = 0;
		if (digito > 9) {
			valor1 = Integer.parseInt(digito.toString().substring(0, 1));
			valor2 = Integer.parseInt(digito.toString().substring(1, 2));
			suma = valor1 + valor2;
			if (suma > 9) {
				suma = sumaDigito(suma);
			}
		}
		return suma;
	}

	private static boolean validaTercero9(String cedula) {
		boolean resultado = false;
		int digito1 = Integer.parseInt(cedula.substring(0, 1));
		int digito2 = Integer.parseInt(cedula.substring(1, 2));
		int digito3 = Integer.parseInt(cedula.substring(2, 3));
		int digito4 = Integer.parseInt(cedula.substring(3, 4));
		int digito5 = Integer.parseInt(cedula.substring(4, 5));
		int digito6 = Integer.parseInt(cedula.substring(5, 6));
		int digito7 = Integer.parseInt(cedula.substring(6, 7));
		int digito8 = Integer.parseInt(cedula.substring(7, 8));
		int digito9 = Integer.parseInt(cedula.substring(8, 9));
		int digito10 = Integer.parseInt(cedula.substring(9));
		int verificador;

		int suma = (digito1 * 4) + (digito2 * 3) + (digito3 * 2) + (digito4 * 7) + (digito5 * 6) + (digito6 * 5)
				+ (digito7 * 4) + (digito8 * 3) + (digito9 * 2);

		verificador = 11 - (suma % 11);
		if (verificador == 11) {
			verificador = 0;
		}

		if (verificador == 10) {
			resultado = false;
		} else if (verificador == digito10) {
			resultado = true;
		}
		return resultado;

	}

	private static boolean validarCadenaNoNumerica(String cadena) {
		return !(cadena.matches("\\d*"));
	}

}

