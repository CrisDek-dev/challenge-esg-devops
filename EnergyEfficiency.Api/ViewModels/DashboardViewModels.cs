namespace EnergyEfficiency.Api.ViewModels;

public class ConsumoSetorResponse
{
    public int SetorId { get; set; }
    public string SetorNome { get; set; } = string.Empty;
    public decimal LimiteMensalKwh { get; set; }
    public decimal ConsumoTotalKwh { get; set; }
    public decimal PercentualUtilizado { get; set; }
    public int QuantidadeEquipamentos { get; set; }
    public int AlertasAbertos { get; set; }
}

public class RelatorioConsumoFilter
{
    public DateTime DataInicio { get; set; } = DateTime.UtcNow.AddDays(-30);
    public DateTime DataFim { get; set; } = DateTime.UtcNow;
}
