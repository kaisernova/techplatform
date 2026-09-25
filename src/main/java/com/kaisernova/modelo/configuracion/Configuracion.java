package com.kaisernova.modelo.configuracion;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
import com.kaisernova.modelo.enums.TipoDato;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor


@Setter
@Entity
@Table(name = "CONFIGURACION", schema = "configuracion")
public class Configuracion extends MappedSuperAuditableActivableClass {
	private NombreParametroConfiguracion nombreParametro;
	private String valor;
	private String descripcion;
	private TipoDato tipoDato;
	//cuando el tipo es ENUM, este campo contiene el nombre del enum
	private String nombreEnumValores;
	private Set<Enum> valoresEnum=new LinkedHashSet<>();

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "NOMBRE_PARAMETRO", unique = true, nullable = false)
	public NombreParametroConfiguracion getNombreParametro() {
		return nombreParametro;
	}

	@Basic
	@Column(name = "VALOR")
	@Lob
	public String getValor() {
		return valor;
	}

	@Basic
	@Column(name = "DESCRIPCION", length = 255)
	public String getDescripcion() {
		return descripcion;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_DATO", nullable = true, length = 32)
	public TipoDato getTipoDato() {
		return tipoDato;
	}

	@Column(name = "NOMBRE_ENUM_VALORES", nullable = true, length = 256)
	public String getNombreEnumValores() {
		return nombreEnumValores;
	}
	
	@Transient
	public Integer getValorAsInteger() {
		if (TipoDato.INTEGER.equals(tipoDato)) {
			try {
				return Integer.valueOf(valor);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
	public void setValorAsInteger(Integer valorInteger) {
		if (Objects.nonNull(valorInteger)) {
			this.valor = String.valueOf(valorInteger);
		} else {
			this.valor = null;
		}
	}

	@Transient
	public Long getValorAsLong() {

		if (TipoDato.LONG.equals(tipoDato)) {
			try {
				return Long.valueOf(valor);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
	public void setValorAsLong(Long valorLong) {
		if (Objects.nonNull(valorLong)) {
			this.valor = String.valueOf(valorLong);
		} else {
			this.valor = null;
		}
	}
	
	@Transient
	public BigDecimal getValorAsBigDecimal() {

		if (TipoDato.BIGDECIMAL.equals(tipoDato)) {
			try {
				return new BigDecimal(valor);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
	public void setValorAsBigDecimal(BigDecimal valorBigDecimal) {
		if (Objects.nonNull(valorBigDecimal)) {
			this.valor = valorBigDecimal.toPlainString();
		} else {
			this.valor = null;
		}
	}

	@Transient
	public Date getValorAsDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TipoDato.DATE.getFormatoDefecto());
		if (TipoDato.DATE.equals(tipoDato)) {
			try {
				return simpleDateFormat.parse(valor);
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}
	
	public void setValorAsDate(Date valorDate) {
		if (Objects.nonNull(valorDate)) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TipoDato.DATE.getFormatoDefecto());
			this.valor = simpleDateFormat.format(valorDate);
		} else {
			this.valor = null;
		}
	}

	@Transient
	public Date getValorAsDateTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TipoDato.DATETIME.getFormatoDefecto());
		if (TipoDato.DATETIME.equals(tipoDato)) {
			try {
				return simpleDateFormat.parse(valor);
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}
	
	public void setValorAsDateTime(Date valorDateTime) {
		if (Objects.nonNull(valorDateTime)) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TipoDato.DATETIME.getFormatoDefecto());
			this.valor = simpleDateFormat.format(valorDateTime);
		} else {
			this.valor = null;
		}
	}

	@Transient
	public Boolean getValorAsBoolean() {
		if (TipoDato.BOOLEAN.equals(tipoDato)) {
			Boolean valorBoolean = Boolean.valueOf(valor);
			if (!valorBoolean) {
				String valorTrim = (Objects.nonNull(valor)) ? valor.trim() : "";
				if ("SI".equalsIgnoreCase(valorTrim) 
						|| "1".equalsIgnoreCase(valorTrim)
						|| "S".equalsIgnoreCase(valorTrim) 
						|| "YES".equalsIgnoreCase(valorTrim)
						|| "Y".equalsIgnoreCase(valorTrim)) {
					valorBoolean = Boolean.TRUE;
				}
			}
			return valorBoolean;
		}
		return Boolean.FALSE;
	}
	
	public void setValorAsBoolean(Boolean valorBoolean) {
		if (Objects.nonNull(valorBoolean)) {
			this.valor = String.valueOf(valorBoolean);
		} else {
			this.valor = null;
		}
	}
	@Transient
	public boolean isMostrarComoStringSimple() {
		return TipoDato.STRING.equals(tipoDato) 
				|| TipoDato.INTEGER.equals(tipoDato) 
				|| TipoDato.ENUM.equals(tipoDato) 				
				|| TipoDato.LONG.equals(tipoDato)
				|| TipoDato.BIGDECIMAL.equals(tipoDato);
	}
	@Transient
	public boolean isTipoFecha() {
		return TipoDato.DATE.equals(tipoDato) || TipoDato.DATETIME.equals(tipoDato);
	}
	@Transient
	public boolean isTextualLargo() {
		return TipoDato.LONGTEXT.equals(tipoDato)  || TipoDato.HTML.equals(tipoDato) || TipoDato.BASE64.equals(tipoDato) || TipoDato.BYTEA.equals(tipoDato);
	}
	@Transient
	public Set<Enum> getValoresEnum() {
		return valoresEnum;
	}

	@Override
	public String toString() {
		return "Configuracion [nombreParametro=" + nombreParametro + ", descripcion=" + descripcion + ", tipoDato="
				+ tipoDato + ", nombreEnumValores=" + nombreEnumValores + ", valoresEnum=" + valoresEnum + "]";
	}
	
	
}
