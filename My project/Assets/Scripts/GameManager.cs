using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using TMPro;
using UnityEngine.SceneManagement;
public class GameManager : MonoBehaviour
{
    public static GameManager instance = null;

    [SerializeField]
    private TextMeshProUGUI text;

    [SerializeField]
    private GameObject gameOverPanel;

    private int coin = 0;

    [HideInInspector]
    public bool isGameOver = false;
    // start메소드보다 한단계 더 앞에 불러온다
    void Awake() {
        if (instance == null)
        {
            instance = this;
        }
    }
    //코인 증가
    public void IncreaseCoin()
    {
        coin += 1;
        text.SetText(coin.ToString());

        if(coin % 5 == 0)
        {
            Player player = FindFirstObjectByType<Player>();
            if(player != null)
            {
                player.Upgrade();
            }
        }
    }

    public void SetGameOver()
    {
        isGameOver = true;

        EnemySpawner enemySpawner = FindFirstObjectByType<EnemySpawner>();
        if(enemySpawner != null)
        {
            enemySpawner.StopEnemyRoutine();
        }
        // 1초후에 panel보여주는
        Invoke("ShowGameOverPanel", 1f);
    }

    void ShowGameOverPanel()
    {
        // SetActive는 비활성화되어있는걸 활성화로 바꾸어준다.
        gameOverPanel.SetActive(true);
    }

    public void PlayAgain()
    {
        SceneManager.LoadScene("SampleScene");
    }
}
