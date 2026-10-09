using System.ComponentModel.DataAnnotations;

namespace EnergyEfficiency.Api.Models;

public class Equipamento
{
    public int Id { get; set; }

    [Required]
    [StringLength(120)]
    public string Nome { get; set; } = string.Empty;

    [Required]
    [StringLength(60)]
    public string NumeroSerie { get; set; } = string.Empty;

    public int SetorId { get; set; }
    public Setor? Setor { get; set; }

    public bool Ativo { get; set; } = true;

    [Range(0.01, double.MaxValue)]
    public decimal PotenciaNominalWatts { get; set; }

    public ICollection<LeituraConsumo> Leituras { get; set; } = new List<LeituraConsumo>();
}
