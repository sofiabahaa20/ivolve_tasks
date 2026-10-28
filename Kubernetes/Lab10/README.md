# Lab 10: Node Isolation Using Taints and Labels in Kubernetes

## Objective

- Apply a taint to a worker node with key `workload`, value `ivolveApp`, and effect `NoSchedule`, so pods are not scheduled on the node unless they have a matching toleration.
- Label the same worker node with `project=ivolve` to identify it for targeted pod scheduling.
- Verify the taint and the label using `kubectl describe node` and `kubectl get nodes --show-labels`.

## Environment

| Item | Value |
|---|---|
| Cluster | kubeadm cluster from Lab 9 (Kubernetes v1.35.9) |
| Control plane | `master-1` |
| Worker | `worker-1` |

## Concepts

- **Taint**: marks a node so that pods without a matching **toleration** are not scheduled on it. The format is `key=value:effect`.
- **`NoSchedule`**: new pods without a matching toleration are not scheduled on the node. Pods already running on the node are not evicted.
- **Label**: a key/value pair attached to a node. Pods can target labeled nodes with `nodeSelector`.

Together, the taint keeps unrelated pods off the node, and the label lets the intended pods find it.

---

## Step 1: Check the cluster and the node state before the change

```bash
kubectl get nodes
kubectl get nodes --show-labels
kubectl describe node worker-1 | grep -i taint
```

Result: both nodes are `Ready`, `worker-1` has `Taints: <none>`, and there is no `project` label.

**Screenshot:** `images/step_01_before.png`

## Step 2: Apply the taint to worker-1

```bash
kubectl taint nodes worker-1 workload=ivolveApp:NoSchedule
```

Output:

```
node/worker-1 tainted
```

| Part | Value |
|---|---|
| key | `workload` |
| value | `ivolveApp` |
| effect | `NoSchedule` |

**Screenshot:** `images/step_02_taint.png`

## Step 3: Label worker-1

```bash
kubectl label nodes worker-1 project=ivolve
```

Output:

```
node/worker-1 labeled
```

**Screenshot:** `images/step_03_label_and_verify.png` (the label and the verification are in the same terminal session)

## Step 4: Verify the taint and the label

```bash
kubectl describe node worker-1 | grep -i -A1 taint
kubectl get nodes --show-labels
```

Result:

- `Taints: workload=ivolveApp:NoSchedule` on `worker-1`.
- `project=ivolve` appears in the labels of `worker-1` (and not on `master-1`).

**Screenshot:** `images/step_03_label_and_verify.png`

---

## Notes

- `NoSchedule` only affects new pods. System pods already running on `worker-1` (`calico-node`, `kube-proxy`) were not affected.
- To remove the taint: `kubectl taint nodes worker-1 workload=ivolveApp:NoSchedule-`
- To remove the label: `kubectl label nodes worker-1 project-`
- To schedule a pod on `worker-1` from now on, its spec needs both a matching toleration and a `nodeSelector`:

  ```yaml
  tolerations:
  - key: "workload"
    operator: "Equal"
    value: "ivolveApp"
    effect: "NoSchedule"
  nodeSelector:
    project: ivolve
  ```
