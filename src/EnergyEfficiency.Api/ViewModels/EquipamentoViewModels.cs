namespace EnergyEfficiency.Api.ViewModels;

public class EquipamentoResponse
{
    public int Id { get; set; }
    public string Nome { get; set; } = string.Empty;
    public string NumeroSerie { get; set; } = string.Empty;
    public int SetorId { get; set; }
    public string SetorNome { get; set; } = string.Empty;
    public bool Ativo { get; set; }
    public decimal PotenciaNominalWatts { get; set; }
}
