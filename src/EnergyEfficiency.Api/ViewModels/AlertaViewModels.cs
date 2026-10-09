namespace EnergyEfficiency.Api.ViewModels;

public class AlertaResponse
{
    public long Id { get; set; }
    public int SetorId { get; set; }
    public string SetorNome { get; set; } = string.Empty;
    public DateTime DataHora { get; set; }
    public decimal ConsumoAcumuladoKwh { get; set; }
    public decimal LimiteKwh { get; set; }
    public decimal PercentualAcimaLimite { get; set; }
    public bool Resolvido { get; set; }
    public DateTime? DataResolucao { get; set; }
}

public class AlertaFilter
{
    public int? SetorId { get; set; }
    public bool? Resolvido { get; set; }
    public int Page { get; set; } = 1;
    public int PageSize { get; set; } = 20;
}
