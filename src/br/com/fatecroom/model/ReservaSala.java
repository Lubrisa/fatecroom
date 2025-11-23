package br.com.fatecroom.model;

/**
 * Representa uma reserva de sala/laboratório.
 * Arquivo: reservas_salas.csv
 * Formato: id_reserva;id_sala;id_usuario;data_reserva;hora_inicio;hora_fim;status;observacao
 */
public class ReservaSala {

    private int idReserva;
    private int idSala;
    private int idUsuario;
    private String dataReserva; // yyyy-MM-dd
    private String horaInicio;  // HH:mm
    private String horaFim;     // HH:mm
    private String status;      // PENDENTE, CONFIRMADA, CANCELADA...
    private String observacao;

    public ReservaSala() {
    }

    public ReservaSala(int idReserva, int idSala, int idUsuario,
                       String dataReserva, String horaInicio, String horaFim,
                       String status, String observacao) {
        this.idReserva = idReserva;
        this.idSala = idSala;
        this.idUsuario = idUsuario;
        this.dataReserva = dataReserva;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.status = status;
        this.observacao = observacao;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(String dataReserva) {
        this.dataReserva = dataReserva;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }


    public String toCsv() {
        return String.join(";",
                String.valueOf(idReserva),
                String.valueOf(idSala),
                String.valueOf(idUsuario),
                dataReserva != null ? dataReserva : "",
                horaInicio != null ? horaInicio : "",
                horaFim != null ? horaFim : "",
                status != null ? status : "",
                observacao != null ? observacao.replace(";", ",") : ""
        );
    }

    public static ReservaSala fromCsv(String line) {
        String[] parts = line.split(";", -1); // -1 pra manter campos vazios
        ReservaSala r = new ReservaSala();
        r.setIdReserva(parseIntSafe(parts, 0));
        r.setIdSala(parseIntSafe(parts, 1));
        r.setIdUsuario(parseIntSafe(parts, 2));
        r.setDataReserva(getSafe(parts, 3));
        r.setHoraInicio(getSafe(parts, 4));
        r.setHoraFim(getSafe(parts, 5));
        r.setStatus(getSafe(parts, 6));
        r.setObservacao(parts.length > 7 ? parts[7] : "");
        return r;
    }

    private static int parseIntSafe(String[] parts, int idx) {
        try {
            if (idx >= parts.length) return 0;
            String s = parts[idx].trim();
            if (s.isEmpty()) return 0;
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private static String getSafe(String[] parts, int idx) {
        return idx < parts.length ? parts[idx] : "";
    }

    @Override
    public String toString() {
        return "ReservaSala{" +
                "idReserva=" + idReserva +
                ", idSala=" + idSala +
                ", idUsuario=" + idUsuario +
                ", dataReserva='" + dataReserva + '\'' +
                ", horaInicio='" + horaInicio + '\'' +
                ", horaFim='" + horaFim + '\'' +
                ", status='" + status + '\'' +
                ", observacao='" + observacao + '\'' +
                '}';
    }
}
