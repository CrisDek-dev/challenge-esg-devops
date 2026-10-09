using System.ComponentModel.DataAnnotations;

namespace EnergyEfficiency.Api.ViewModels;

public class LeituraCreateRequest
{
    [Required]
    public int EquipamentoId { get; set; }

    [Required]
    public DateTime DataHora { get; set; }

    [Range(0.0, double.MaxValue, ErrorMessage = "O consumo nao pode ser negativo")]
    public decimal ConsumoKwh { get; set; }
}

public class LeituraResponse
{
    public long Id { get; set; }
    public int EquipamentoId { get; set; }
    public string EquipamentoNome { get; set; } = string.Empty;
    public string SetorNome { get; set; } = string.Empty;
    public DateTime DataHora { get; set; }
    public decimal ConsumoKwh { get; set; }
    public bool GerouAlerta { get; set; }
}

public class LeituraFilter
{
    public int? EquipamentoId { get; set; }
    public int? SetorId { get; set; }
    public DateTime? DataInicio { get; set; }
    public DateTime? DataFim { get; set; }
    public int Page { get; set; } = 1;
    public int PageSize { get; set; } = 20;
}
