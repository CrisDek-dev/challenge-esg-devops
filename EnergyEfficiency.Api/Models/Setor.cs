using System.ComponentModel.DataAnnotations;

namespace EnergyEfficiency.Api.Models;

public class Setor
{
    public int Id { get; set; }

    [Required]
    [StringLength(120)]
    public string Nome { get; set; } = string.Empty;

    [StringLength(500)]
    public string? Descricao { get; set; }

    [Range(0.01, double.MaxValue)]
    public decimal LimiteConsumoMensalKwh { get; set; }

    public ICollection<Equipamento> Equipamentos { get; set; } = new List<Equipamento>();
    public ICollection<AlertaConsumo> Alertas { get; set; } = new List<AlertaConsumo>();
}
