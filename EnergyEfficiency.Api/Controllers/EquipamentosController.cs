using EnergyEfficiency.Api.Services;
using EnergyEfficiency.Api.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace EnergyEfficiency.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class EquipamentosController : ControllerBase
{
    private readonly IEquipamentoService _service;

    public EquipamentosController(IEquipamentoService service) => _service = service;

    /// <summary>
    /// Detalhes de um equipamento monitorado.
    /// </summary>
    [HttpGet("{id:int}")]
    [ProducesResponseType(typeof(EquipamentoResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<EquipamentoResponse>> Obter(int id, CancellationToken ct)
    {
        var result = await _service.ObterAsync(id, ct);
        return Ok(result);
    }

    /// <summary>
    /// Desliga remotamente o equipamento (acao critica, requer perfil ADMIN).
    /// Equipamentos desligados nao aceitam novas leituras.
    /// </summary>
    [HttpPost("{id:int}/desligar")]
    [Authorize(Roles = "ADMIN")]
    [ProducesResponseType(typeof(EquipamentoResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<EquipamentoResponse>> Desligar(int id, CancellationToken ct)
    {
        var result = await _service.AlterarEstadoAsync(id, false, ct);
        return Ok(result);
    }

    /// <summary>
    /// Liga remotamente um equipamento desligado (requer perfil ADMIN).
    /// </summary>
    [HttpPost("{id:int}/ligar")]
    [Authorize(Roles = "ADMIN")]
    [ProducesResponseType(typeof(EquipamentoResponse), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<EquipamentoResponse>> Ligar(int id, CancellationToken ct)
    {
        var result = await _service.AlterarEstadoAsync(id, true, ct);
        return Ok(result);
    }
}
